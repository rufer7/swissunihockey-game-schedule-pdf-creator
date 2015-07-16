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

import net.fortuna.ical4j.model.Calendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PDFGeneratorTest {

    public static final String SAMPLE_CLUB_NAME = "Sample Club";
    private PDFGenerator pdfGenerator;
    private String fileName;

    @Before
    public void init() {
        pdfGenerator = new PDFGenerator();
    }

    @After
    public void cleanup() {
        File file = new File("./" + fileName);
        file.delete();
    }

    @Test
    public void createPDFBasedCalendarForClubReturnsFileName() {
        fileName = pdfGenerator.createPDFBasedCalendarForClub(new Calendar(), SAMPLE_CLUB_NAME);
        assertTrue(fileName.contains(SAMPLE_CLUB_NAME));
    }

    @Test
    public void createPDFBasedCalendarForClubCreatesPDFDocument() throws IOException {
        fileName = pdfGenerator.createPDFBasedCalendarForClub(new Calendar(), SAMPLE_CLUB_NAME);
        InputStream inputStream = new FileInputStream("./" + fileName);
        assertNotNull(inputStream);
        inputStream.close();
    }
}
