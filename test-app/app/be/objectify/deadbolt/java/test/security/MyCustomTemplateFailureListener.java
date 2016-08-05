/*
 * Copyright 2010-2016 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.objectify.deadbolt.java.test.security;

import be.objectify.deadbolt.java.TemplateFailureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * @author Steve Chaloner (steve@objectify.be)
 */
@Singleton
public class MyCustomTemplateFailureListener implements TemplateFailureListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MyCustomTemplateFailureListener.class);

    @Override
    public void failure(final String message,
                        final long timeout)
    {
        LOGGER.error("Template constraint failure: message [{}]  timeout [{}]ms",
                     message,
                     timeout);
    }
}
