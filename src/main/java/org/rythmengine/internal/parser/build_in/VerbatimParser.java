/**
 * Copyright (C) 2013-2016 The Rythm Engine project
 * for LICENSE and other details see:
 * https://github.com/rythmengine/rythmengine
 */
package org.rythmengine.internal.parser.build_in;

/*-
 * #%L
 * Rythm Template Engine
 * %%
 * Copyright (C) 2017 - 2021 OSGL (Open Source General Library)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.stevesoft.pat.Regex;
import org.rythmengine.internal.IContext;
import org.rythmengine.internal.IParser;
import org.rythmengine.internal.Keyword;
import org.rythmengine.internal.Token;
import org.rythmengine.internal.parser.ParserBase;
import org.rythmengine.utils.S;

public class VerbatimParser extends KeywordParserFactory {

    private static final String R = "(^\\n?[ \\t\\x0B\\f]*%s%s\\s*(\\(\\s*\\))?\\s*((?@{})))";

    public VerbatimParser() {
    }

    protected String patternStr() {
        return R;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public Token go() {
                Regex r = reg(dialect());
                if (r.search(remain())) {
                    final String matched = r.stringMatched();
                    step(matched.length());
                    String s0 = r.stringMatched(3);
                    s0 = S.strip(s0, "{", "}");
                    s0 = S.strip(s0, "\n", "\n");
                    final String s = s0;
                    return new Token(s, ctx(), true) {
                        @Override
                        protected void output() {
                            s = compact(s);
                            s = s.replaceAll("(\\r?\\n)+", "\\\\n").replaceAll("\"", "\\\\\"");
                            p("p(org.rythmengine.utils.S.raw(\"").p(s).p("\"));");
                            pline();
                        }
                    };
                }
                return null;
            }
        };
    }

    @Override
    public Keyword keyword() {
        return Keyword.VERBATIM;
    }
}
