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

import be.rufer.swissunihockey.TestConstants;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PDFGeneratorTest {

    private static Calendar sampleTeamCalendar;
    private PDFGenerator pdfGenerator;
    private String fileName;

    @BeforeClass
    public static void setup() throws IOException, ParserException {
        InputStream inputStream = new FileInputStream(
                new File(PDFGeneratorTest.class.getClassLoader().getResource("team-calendar-response.txt").getFile()));
        CalendarBuilder builder = new CalendarBuilder();
        sampleTeamCalendar = builder.build(inputStream);
    }

    @Before
    public void init() {
        pdfGenerator = new PDFGenerator();
    }

    @After
    public void cleanup() {
        File file = new File(String.format("./%s", fileName));
        boolean result = file.delete();
        assertTrue(result);
    }

    @Test
    public void createPDFBasedCalendarForTeamReturnsFileName() {
        fileName = pdfGenerator.createPDFBasedCalendarForTeam(sampleTeamCalendar, TestConstants.TEAM_NAME);
        assertTrue(fileName.contains(TestConstants.TEAM_NAME));
    }

    @Test
    public void createPDFBasedCalendarForTeamCreatesPDFDocument() throws IOException {
        fileName = pdfGenerator.createPDFBasedCalendarForTeam(sampleTeamCalendar, TestConstants.TEAM_NAME);
        InputStream inputStream = new FileInputStream(String.format("./%s", fileName));
        assertNotNull(inputStream);
        inputStream.close();
    }
}
