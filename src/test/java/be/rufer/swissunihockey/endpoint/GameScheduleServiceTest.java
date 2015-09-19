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
package be.rufer.swissunihockey.endpoint;

import be.rufer.swissunihockey.TestConstants;
import be.rufer.swissunihockey.client.SwissunihockeyAPIClient;
import be.rufer.swissunihockey.pdf.PDFGenerator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameScheduleServiceTest {

    private static final String SAMPLE_FILE_NAME = "Hornets R.Moosseedorf Worblental-1437314588.pdf";
    private static final String FILE_FORMAT = "UTF-8";
    private static final String ACTUAL_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    private static final String ROOT_DIRECTORY = "./";

    @InjectMocks
    private GameScheduleService gameScheduleService;

    @Mock
    private SwissunihockeyAPIClient swissunihockeyAPIClient;

    @Mock
    private PDFGenerator pdfGenerator;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void postConstructMethodCallsSwissunihockeyAPIClientForGettingClubsOfActualSeason() {
        gameScheduleService.initClubMap();
        verify(swissunihockeyAPIClient).getClubsOfSeason(ACTUAL_YEAR);
    }

    @Test
    public void postConstructMethodInitializesClubMapWithDataFromSwissunihockeyAPIClient() {
        Map<String, String> clubs = new HashMap<>();
        clubs.put(TestConstants.CLUB_ID, TestConstants.CLUB_NAME);
        when(swissunihockeyAPIClient.getClubsOfSeason(ACTUAL_YEAR)).thenReturn(clubs);
        gameScheduleService.initClubMap();
        assertNotNull(GameScheduleService.clubs);
        assertEquals(clubs, GameScheduleService.clubs);
    }

    @Test
    public void createPDFGameScheduleForTeamCallsSwissunihockeyAPIClientForGettingTeamsCalendar() {
        gameScheduleService.createPDFGameScheduleForTeam(TestConstants.CLUB_ID, TestConstants.TEAM_ID);
        verify(swissunihockeyAPIClient).getCalendarForTeam(TestConstants.TEAM_ID);
    }

    @Test
    public void createPDFGameScheduleForTeamCallsPDFGeneratorWithCalendarFetchedFromAPI() {
        initClubMap();
        when(swissunihockeyAPIClient.getCalendarForTeam(TestConstants.TEAM_ID)).thenReturn(new net.fortuna.ical4j.model.Calendar());
        gameScheduleService.createPDFGameScheduleForTeam(TestConstants.CLUB_ID, TestConstants.TEAM_ID);
        verify(pdfGenerator).createPDFBasedCalendarForTeam(any(net.fortuna.ical4j.model.Calendar.class), eq(TestConstants.CLUB_NAME));
    }

    private void initClubMap() {
        Map<String, String> clubs = new HashMap<>();
        clubs.put(TestConstants.CLUB_ID, TestConstants.CLUB_NAME);
        GameScheduleService.clubs = clubs;
    }

    @Test
    public void deleteOldUnusedFilesDeletesUnusedFilesOlderThanFifteenMinutesInRootDirectory()
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(SAMPLE_FILE_NAME, FILE_FORMAT);
        writer.close();

        File file = new File(ROOT_DIRECTORY + SAMPLE_FILE_NAME);
        boolean lastModifiedSet = file.setLastModified(LocalDate.now().minusDays(1).toEpochDay());

        gameScheduleService.deleteOldUnusedFiles();
        assertTrue(lastModifiedSet);
        assertFalse((new File(String.format("./%s", SAMPLE_FILE_NAME)).exists()));
    }

    @Test
    public void deleteOldUnusedFilesNotDeletingFilesCreatedOrModifiedInTheLastFifteenMinutes()
            throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(SAMPLE_FILE_NAME, FILE_FORMAT);
        writer.close();
        gameScheduleService.deleteOldUnusedFiles();
        assertTrue((new File(String.format("./%s", SAMPLE_FILE_NAME)).exists()));

        // CLEANUP
        File file = new File(ROOT_DIRECTORY + SAMPLE_FILE_NAME);
        boolean cleanupSuccess = file.delete();
        assertTrue(cleanupSuccess);
    }
}
