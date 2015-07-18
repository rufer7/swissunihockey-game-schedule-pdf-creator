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

import be.rufer.swissunihockey.client.SwissunihockeyAPIClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameScheduleServiceTest {

    private static final String SAMPLE_FILE_NAME = "sample-file.txt";
    private static final String FILE_FORMAT = "UTF-8";
    private static final String ACTUAL_YEAR = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

    @InjectMocks
    private GameScheduleService gameScheduleService;

    @Mock
    private SwissunihockeyAPIClient swissunihockeyAPIClient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void postConstructMethodCallsSwissunihockeyAPIClientForGettingClubsOfActualSeason() {
        gameScheduleService.initMaps();
        verify(swissunihockeyAPIClient).getClubsOfSeason(ACTUAL_YEAR);
    }

    @Test
    public void postConstructMethodInitializesClubMapWithDataFromSwissunihockeyAPIClient() {
        Map<String, String> clubs = new HashMap();
        clubs.put("1", "Sample Club");
        when(swissunihockeyAPIClient.getClubsOfSeason(ACTUAL_YEAR)).thenReturn(clubs);
        gameScheduleService.initMaps();
        assertNotNull(GameScheduleService.clubs);
        assertEquals(clubs, GameScheduleService.clubs);
    }

    @Test
    public void createPDFGameScheduleForTeam() {
        // TODO extend test method name
    }

    @Test
    public void deleteFileDeletesFileWithGivenName() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(SAMPLE_FILE_NAME, FILE_FORMAT);
        writer.close();
        gameScheduleService.deleteFile(SAMPLE_FILE_NAME);
        assertFalse((new File(String.format("./%s", SAMPLE_FILE_NAME)).exists()));
    }
}
