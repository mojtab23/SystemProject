package trash.server2;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Mojtaba on 4/9/2015.
 */
public class Server2Test {

    @Test
    public void testServer2() throws Exception {

        new Server2(10000);
        Assert.assertTrue(true);

    }
}