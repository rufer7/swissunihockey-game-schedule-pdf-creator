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
import net.fortuna.ical4j.model.Calendar;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

/**
 * Generator class that provides methods for creation of PDF documents.
 */
public class PDFGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(PDFGenerator.class);
    private PDFont font;

    public PDFGenerator() {
        font = PDType1Font.HELVETICA_BOLD;
    }

    public String createPDFBasedCalendarForClub(Calendar clubCalendar, String clubName) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        return writeClubCalendarToPDFAndSave(document, page, clubCalendar, clubName);
    }

    private String writeClubCalendarToPDFAndSave(PDDocument document, PDPage page, Calendar calendar, String clubName) {

        String fileName = generateUniqueFileName(clubName);

        PDPageContentStream contentStream = null;
        try {
            contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.moveTextPositionByAmount(100, 700);
            contentStream.drawString("Hello World");
            contentStream.endText();

            contentStream.close();

            document.save(fileName);
            document.close();
        } catch (IOException e) {
            LOG.error("Error occurred while creating PDF document", e);
            throw new PDFCreationException();
        } catch (COSVisitorException e) {
            LOG.error("Error occurred while saving the PDF document", e);
            throw new PDFCreationException();
        }

        return fileName;
    }

    protected String generateUniqueFileName(String prefix) {
        return String.format("%s-%s.pdf", prefix, Instant.now().getEpochSecond());
    }
}
