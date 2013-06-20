package ut.com.change_vision.test;

import org.junit.Test;
import com.change_vision.test.MyPluginComponent;
import com.change_vision.test.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}