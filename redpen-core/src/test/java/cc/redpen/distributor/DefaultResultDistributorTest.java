/**
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
package cc.redpen.distributor;

import cc.redpen.RedPenException;
import cc.redpen.formatter.PlainFormatter;
import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import cc.redpen.validator.Validator;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class DefaultResultDistributorTest extends Validator<Sentence> {
    @Test
    public void testFlushHeaderWithPlainFormatter() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        distributor.flushFooter();
        String result = null;
        try {
            result = new String(os.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("", result);
    }

    @Test
    public void testFlushFooterWithPlainFormatter() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        distributor.flushFooter();
        String result = null;
        try {
            result = new String(os.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("", result);
    }

    @Test
    public void testFlushErrorWithPlainFormatter() throws RedPenException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        ValidationError error = createValidationError(-1);
        distributor.flushError(error);
        String result = null;
        try {
            result = new String(os.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            fail();
        }
        Pattern p = Pattern.compile("foobar");
        Matcher m = p.matcher(result);
        assertTrue(m.find());
    }

    @Test(expected = RedPenException.class)
    public void testFlushErrorWithPlainFormatterForNull() throws RedPenException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DefaultResultDistributor distributor = new DefaultResultDistributor(os);
        distributor.setFormatter(new PlainFormatter());
        distributor.flushError(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePlainFormatterNullStream() {
        DefaultResultDistributor distributor = new DefaultResultDistributor(null);
        distributor.setFormatter(new PlainFormatter());
    }

    @Override
    public List<ValidationError> validate(Sentence block) {
        return null;
    }
}
