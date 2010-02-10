/*
 * Copyright 2009 Martin Grotzke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.javakaffee.web.msm.serializer.javolution;

import javolution.xml.XMLFormat;
import net.spy.memcached.transcoders.Transcoder;

import org.apache.catalina.Loader;
import org.apache.catalina.Manager;

import de.javakaffee.web.msm.TranscoderFactory;

/**
 * Creates {@link XStreamTranscoder} instances.
 * 
 * @author Martin Grotzke (martin.grotzke@freiheit.com) (initial creation)
 */
public class JavolutionTranscoderFactory implements TranscoderFactory {

    private boolean _copyCollectionsForSerialization;
    private String[] _customConverterClassNames;

    /**
     * {@inheritDoc}
     */
    public Transcoder<Object> createTranscoder( final Manager manager ) {
        final XMLFormat<?>[] customFormats = loadCustomFormats( manager );
        return new JavolutionTranscoder( manager, _copyCollectionsForSerialization, customFormats );
    }

    private XMLFormat<?>[] loadCustomFormats( final Manager manager ) {
        if ( _customConverterClassNames == null || _customConverterClassNames.length == 0 ) {
            return null;
        }
        final XMLFormat<?>[] customFormats = new XMLFormat<?>[ _customConverterClassNames.length ];
        final Loader loader = manager.getContainer().getLoader();
        for ( int i = 0; i < _customConverterClassNames.length; i++ ) {
            final String className = _customConverterClassNames[i];
            try {
                final XMLFormat<?> xmlFormat = Class.forName( className, true, loader.getClassLoader() ).asSubclass( XMLFormat.class ).newInstance();
                customFormats[i] = xmlFormat;
            } catch ( final Exception e ) {
                throw new RuntimeException( "Could not load custom xml format " + className, e );
            }
        }
        return customFormats;
    }

    /**
     * {@inheritDoc}
     */
    public void setCopyCollectionsForSerialization( final boolean copyCollectionsForSerialization ) {
        _copyCollectionsForSerialization = copyCollectionsForSerialization;
    }

    /**
     * {@inheritDoc}
     */
    public void setCustomConverterClassNames( final String[] customConverterClassNames ) {
        _customConverterClassNames = customConverterClassNames;
    }

}