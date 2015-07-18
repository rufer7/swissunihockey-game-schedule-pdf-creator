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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;

@Service
public class GameScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(GameScheduleService.class);

    @PostConstruct
    public void postConstruct() {
        // TODO #14
    }

    public String createPDFGameScheduleForTeam(String clubId, String teamId) {
        // TODO return file name
        // TODO resolve club name from id
        return null;
    }

    public void deleteFile(String fileName) {
        LOG.info("Delete file with name '{}'", fileName);
        File file = new File(String.format("./%s", fileName));
        if (file.delete()) {
            LOG.info("File with name '{}' deleted", fileName);
        }
    }
}
