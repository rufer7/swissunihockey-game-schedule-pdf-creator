/*
 * Copyright (C) 2015 Marc Rufer (m.rufer@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.rufer.swissunihockey.pdf;

import be.rufer.swissunihockey.pdf.exception.PDFCreationException;
import net.fortuna.ical4j.model.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Generator class that provides methods for creation of PDF documents.
 */
@Service
public class PDFGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(PDFGenerator.class);
    private static final int TITLE_FONT_SIZE = 14;
    private static final int OVERVIEW_FONT_SIZE = 12;
    private static final int CONTENT_FONT_SIZE = 10;
    private static final int X_ALIGNMENT = 30;
    private static final int Y_ALIGNMENT_TITLE = 550;
    private static final int Y_ALIGNMENT_GAMES = 490;
    private static final int Y_ALIGNMENT_OVERVIEW = 520;
    private static final int Y_ALIGNMENT_FOOTER = 30;
    private static final int LINE_DISTANCE = 15;
    private static final int ROTATION = 90;
    private static final int ZERO = 0;
    private static final String CALENDAR_DATE_FORMAT = "yyyyMMdd'T'HHmmss";
    private static final String GAME_SCHEDULE_DATE_FORMAT = "dd.MM.yyyy HH:mm";
    private static final String TEAM_DELIMITER = " - ";
    private static PDFont font;

    public PDFGenerator() {
        font = PDType1Font.HELVETICA_BOLD;
    }

    public String createPDFBasedCalendarForTeam(Calendar teamCalendar, String teamName) {
        PDDocument document = new PDDocument();
        try {
            return writeTeamCalendarToPDFAndSave(document, teamCalendar, teamName);
        } catch (IOException e) {
            LOG.error("Error occurred while closing document", e);
            throw new PDFCreationException();
        }
    }

    private String writeTeamCalendarToPDFAndSave(PDDocument document, Calendar calendar, String teamName) throws IOException {
        PDPage page = new PDPage();
        page.setMediaBox(PDRectangle.A4);
        page.setRotation(ROTATION);
        document.addPage(page);

        String fileName = generateUniqueFileName(teamName);

        PDPageContentStream contentStream;
        try {
            contentStream = new PDPageContentStream(document, page);
            contentStream.transform(new Matrix(ZERO, 1, -1, ZERO, page.getMediaBox().getWidth(), ZERO));

            writeTitle(contentStream, PDFTemplates.TEAM_SCHEDULE_TITLE, teamName);
            writeTeamOverview(contentStream, calendar);
            writeTeamCalendarContent(contentStream, calendar);
            writeFooter(contentStream, PDFTemplates.FOOTER_TEXT);

            contentStream.close();
            document.save(fileName);
        } catch (IOException e) {
            LOG.error("Error occurred while creating or saving the PDF document", e);
            throw new PDFCreationException();
        } finally {
            document.close();
        }
        return fileName;
    }

    private void writeTitle(PDPageContentStream contentStream, String template, String... templateVariables) throws IOException {
        contentStream.setFont(font, TITLE_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(X_ALIGNMENT, Y_ALIGNMENT_TITLE);
        contentStream.showText(String.format(template, templateVariables));
        contentStream.endText();
    }

    private void writeTeamOverview(PDPageContentStream contentStream, Calendar calendar) throws IOException {
        contentStream.setFont(font, OVERVIEW_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(X_ALIGNMENT, Y_ALIGNMENT_OVERVIEW);
        contentStream.showText(getOverviewText(calendar));
        contentStream.endText();
    }

    private String getOverviewText(Calendar calendar) {
        String overviewText = "";
        for (Object o : calendar.getComponents()) {
            if (((Component) o).getName() == Component.VTIMEZONE)
            {
                continue;
            }
            Property property = ((Component) o).getProperties().getProperty(Property.DESCRIPTION);
            if (property.getValue().contains("Runde ")) {
                overviewText = property.getValue().replace("\\", "").replaceAll("Runde\\s+\\d,+\\s", "");
                break;
            }
        }
        return overviewText;
    }

    private void writeTeamCalendarContent(PDPageContentStream contentStream, Calendar calendar) throws IOException {
        LOG.info("Start writing calendar content to content stream...");
        contentStream.setFont(font, CONTENT_FONT_SIZE);

        int yPosition = Y_ALIGNMENT_GAMES;

        for (Object component : calendar.getComponents()) {
            if (((Component)component).getName() == Component.VTIMEZONE)
            {
                continue;
            }
            PropertyList properties = ((Component)component).getProperties();

            contentStream.beginText();
            contentStream.newLineAtOffset(X_ALIGNMENT, yPosition);
            if (properties.getProperty(Property.DTSTART) != null) {
                contentStream.showText(formatDate(properties.getProperty(Property.DTSTART).getValue()));
            }
            String summary = properties.getProperty(Property.SUMMARY).getValue();
            contentStream.newLineAtOffset(100, ZERO);
            contentStream.showText(getHomeTeamName(summary));
            contentStream.newLineAtOffset(220, ZERO);
            contentStream.showText(getAwayTeamName(summary));
            contentStream.newLineAtOffset(220, ZERO);
            contentStream.showText(properties.getProperty(Property.LOCATION).getValue());
            contentStream.endText();

            yPosition -= LINE_DISTANCE;
        }
        LOG.info("Calendar data successfully written to content stream");
    }

    private String getHomeTeamName(String summary) {
        if (summary.contains(TEAM_DELIMITER))
        {
            return summary.substring(0, summary.indexOf(TEAM_DELIMITER));
        }
        return summary;
    }

    private String getAwayTeamName(String summary) {
        if (summary.contains(TEAM_DELIMITER))
        {
            return summary.substring(summary.indexOf(TEAM_DELIMITER) + 3);
        }
        return summary;
    }

    private String formatDate(String dateAsString) {
        LocalDateTime timeZoneAwareDateTime = parseDate(dateAsString);
        return timeZoneAwareDateTime.format(DateTimeFormatter.ofPattern(GAME_SCHEDULE_DATE_FORMAT));
    }

    private LocalDateTime parseDate(String dateAsString) {
        try {
            return LocalDateTime.parse(dateAsString, DateTimeFormatter.ofPattern(CALENDAR_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            LOG.error("Error occurred while parsing string representation of date: {}", dateAsString);
            throw new PDFCreationException();
        }
    }

    private void writeFooter(PDPageContentStream contentStream, String footerText) throws IOException {
        contentStream.setFont(font, CONTENT_FONT_SIZE);
        contentStream.beginText();
        contentStream.newLineAtOffset(X_ALIGNMENT, Y_ALIGNMENT_FOOTER);
        contentStream.showText(footerText);
        contentStream.endText();
    }

    protected String generateUniqueFileName(String prefix) {
        return String.format("%s-%s.pdf", prefix, Instant.now().getEpochSecond());
    }
}
