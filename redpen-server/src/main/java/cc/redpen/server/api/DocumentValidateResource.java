/*
 * redpen: a text inspection tool
 * Copyright (C) 2014 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.redpen.server.api;

import cc.redpen.RedPen;
import cc.redpen.RedPenException;
import cc.redpen.model.Document;
import cc.redpen.model.DocumentCollection;
import cc.redpen.parser.DocumentParser;
import cc.redpen.parser.DocumentParserFactory;
import cc.redpen.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource to validate documents.
 */
@Path("/document")
public class DocumentValidateResource {

    private static final Logger LOG = LogManager.getLogger(
            DocumentValidateResource.class
    );
    private final static String DEFAULT_INTERNAL_CONFIG_PATH = "/conf/redpen-conf.xml";
    @Context
    private ServletContext context;

    private Map<String, RedPen> langRedPenMap = new HashMap<>();

    private RedPen getRedPen(String lang) {
        if (langRedPenMap.size() == 0) {
            synchronized (this) {
                if (langRedPenMap.size() == 0) {
                    LOG.info("Starting Document Validator Server.");
                    try {
                        RedPen japaneseRedPen = new RedPen.Builder().setConfigPath("/conf/redpen-conf-ja.xml").build();
                        langRedPenMap.put("ja", japaneseRedPen);
                        RedPen englishRedPen = new RedPen.Builder().setConfigPath(DEFAULT_INTERNAL_CONFIG_PATH).build();
                        langRedPenMap.put("en", englishRedPen);
                        langRedPenMap.put("", englishRedPen);

                        String configPath;
                        if (context != null) {
                            configPath = context.getInitParameter("redpen.conf.path");
                            if (configPath != null) {
                                LOG.info("Config Path is set to \"{}\"", configPath);
                                RedPen defaultRedPen = new RedPen.Builder().setConfigPath(configPath).build();
                                langRedPenMap.put("", defaultRedPen);
                            } else {
                                // if config path is not set, fallback to default config path
                                LOG.info("Config Path is set to \"{}\"", DEFAULT_INTERNAL_CONFIG_PATH);
                            }
                        }
                        LOG.info("Document Validator Server is running.");
                    } catch (RedPenException e) {
                        LOG.error("Unable to initialize RedPen", e);
                        throw new ExceptionInInitializerError(e);
                    }
                }
            }
        }
        return langRedPenMap.getOrDefault(lang, langRedPenMap.get(""));
    }

    @Path("/validate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response validateDocument(@FormParam("textarea") @DefaultValue("") String document,
                                     @FormParam("lang") @DefaultValue("en") String lang)
            throws JSONException, RedPenException, UnsupportedEncodingException {

        LOG.info("Validating document");
        RedPen server = getRedPen(lang);
        System.out.println(document);
        JSONObject json = new JSONObject();

        json.put("document", document);

        DocumentParser parser = DocumentParserFactory.generate(
                DocumentParser.Type.PLAIN, server.getConfiguration(), new DocumentCollection.Builder());
        Document fileContent = parser.generateDocument(new
                ByteArrayInputStream(document.getBytes("UTF-8")));

        DocumentCollection d = new DocumentCollection();
        d.addDocument(fileContent);

        List<ValidationError> errors = server.check(d);

        JSONArray jsonErrors = new JSONArray();

        for (ValidationError error : errors) {
            JSONObject jsonError = new JSONObject();
            if (error.getSentence().isPresent()) {
                jsonError.put("sentence", error.getSentence().get().content);
            }
            jsonError.put("message", error.getMessage());
            jsonErrors.put(jsonError);
        }

        json.put("errors", jsonErrors);

        return Response.ok().entity(json).build();
    }
}
