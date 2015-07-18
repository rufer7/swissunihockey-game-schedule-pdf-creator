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

import be.rufer.swissunihockey.endpoint.exception.ServePDFException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GameScheduleControllerTest {

    private static final String CLUB_ID = "1";
    private static final String TEAM_ID = "2";
    public static final String SAMPLE_PDF_FILE_NAME = "Sample-game-schedule.pdf";

    @Mock
    private GameScheduleService gameScheduleService;

    @InjectMocks
    private GameScheduleController controller;

    @Test
    public void getPDFGameScheduleOfTeamCallsGameScheduleService() {
        when(gameScheduleService.createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID)).thenReturn(SAMPLE_PDF_FILE_NAME);
        controller.getPDFGameScheduleOfTeam(CLUB_ID, TEAM_ID);
        verify(gameScheduleService).createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID);
    }

    @Test
    public void getPDFGameScheduleOfTeamReturnsInputStreamResource() {
        when(gameScheduleService.createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID)).thenReturn(SAMPLE_PDF_FILE_NAME);
        ResponseEntity<InputStreamResource> response = controller.getPDFGameScheduleOfTeam(CLUB_ID, TEAM_ID);
        verify(gameScheduleService).createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof InputStreamResource);
    }

    @Test(expected = ServePDFException.class)
    public void getPDFGameScheduleOfTeamThrowsExceptionForNonExistingFileName() {
        when(gameScheduleService.createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID)).thenReturn("Non existing file name");
        controller.getPDFGameScheduleOfTeam(CLUB_ID, TEAM_ID);
    }

    @Test
    public void getPDFGameScheduleOfTeamCallsDeleteFileMethodOfGameScheduleService() {
        when(gameScheduleService.createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID)).thenReturn(SAMPLE_PDF_FILE_NAME);
        controller.getPDFGameScheduleOfTeam(CLUB_ID, TEAM_ID);
        verify(gameScheduleService).createPDFGameScheduleForTeam(CLUB_ID, TEAM_ID);
        verify(gameScheduleService).deleteFile(SAMPLE_PDF_FILE_NAME);
    }
}
