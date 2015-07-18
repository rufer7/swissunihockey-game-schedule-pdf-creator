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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api")
public class GameScheduleController {

    private static final Logger LOG = LoggerFactory.getLogger(GameScheduleService.class);

    @Autowired
    private GameScheduleService gameScheduleService;

    @RequestMapping(value = "clubs/{clubId}/teams/{teamId}/game-schedule", produces = "application/pdf")
    public ResponseEntity<InputStreamResource> getPDFGameScheduleOfTeam(@PathVariable("clubId") String clubId,
                                                                        @PathVariable("teamId") String teamId) {

        String fileName = gameScheduleService.createPDFGameScheduleForTeam(clubId, teamId);
        FileSystemResource pdfFile = new FileSystemResource(String.format("./%s", fileName));

        try {
            return ResponseEntity
                    .ok()
                    .contentLength(pdfFile.contentLength())
                    .contentType(MediaType.parseMediaType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                    .body(new InputStreamResource(pdfFile.getInputStream()));
        } catch (IOException e) {
            LOG.error("Error occurred while loading pdf file '" + fileName + "' from file system", e);
            throw new ServePDFException();
        } finally {
            gameScheduleService.deleteFile(fileName);
        }
    }
}
