/*
 * Copyright (c) 2016 Matsv
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package viaversion.viabackwards.protocol.protocol1_12_1to1_12_2;

import viaversion.viaversion.api.data.StoredObject;
import viaversion.viaversion.api.data.UserConnection;

public class KeepAliveTracker extends StoredObject {
    private long keepAlive = Integer.MAX_VALUE;

    public KeepAliveTracker(UserConnection user) {
        super(user);
    }

    public long getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(long keepAlive) {
        this.keepAlive = keepAlive;
    }

    @Override
    public String toString() {
        return "KeepAliveTracker{" + "keepAlive=" + keepAlive + '}';
    }
}
