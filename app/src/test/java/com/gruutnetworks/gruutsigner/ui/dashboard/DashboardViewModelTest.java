package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.util.Base64;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import com.gruutnetworks.gruutsigner.util.AuthUtil;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

@PrepareForTest({Base64.class})
public class DashboardViewModelTest extends RobolectricTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void byteHandingTest() {
        String time = AuthUtil.getTimestamp();
        byte[] sigSender = ByteBuffer.allocate(8).putLong(Integer.parseInt("15")).array();
        byte[] sigTime = ByteBuffer.allocate(8).putLong(Integer.parseInt(time)).array();
        byte[] sigHgt = ByteBuffer.allocate(8).putLong(Integer.parseInt("1")).array();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(sigSender);
            outputStream.write(sigTime);
            outputStream.write(Base64.decode("TUVSR0VSLTE=", Base64.NO_WRAP));
            outputStream.write(sigHgt);
            outputStream.write(Base64.decode("R0VOVEVTVDE=", Base64.NO_WRAP));

            String str = outputStream.toString();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}