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
package cc.redpen.validator.sentence;

import cc.redpen.model.Sentence;
import cc.redpen.validator.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommaNumberValidatorTest {

    @Test
    public void testWithSentenceContainingManyCommas() {
        CommaNumberValidator commaNumberValidator = new CommaNumberValidator();
        String content = "is it true, not true, but it should be ture, right, or not right.";
        Sentence str = new Sentence(content, 0);
        List<ValidationError> errors = commaNumberValidator.validate(str);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(content, errors.get(0).getSentence().get().content);
    }

    @Test
    public void testWithtSentenceWithoutComma() {
        CommaNumberValidator commaNumberValidator = new CommaNumberValidator();
        String content = "is it true.";
        Sentence str = new Sentence(content, 0);
        List<ValidationError> errors = commaNumberValidator.validate(str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }

    @Test
    public void testWithtZeroLengthSentence() {
        CommaNumberValidator commaNumberValidator = new CommaNumberValidator();
        String content = "";
        Sentence str = new Sentence(content, 0);
        List<ValidationError> errors = commaNumberValidator.validate(str);
        assertNotNull(errors);
        assertEquals(0, errors.size());
    }
}
