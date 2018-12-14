package com.gruutnetworks.gruutsigner.ui.dashboard;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.gruutnetworks.gruutsigner.RobolectricTest;
import com.gruutnetworks.gruutsigner.model.SignedBlock;
import com.gruutnetworks.gruutsigner.model.SignedBlockDao;
import com.gruutnetworks.gruutsigner.util.AppDatabase;
import com.gruutnetworks.gruutsigner.util.AuthGeneralUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@PrepareForTest({Base64.class})
public class DashboardViewModelTest extends RobolectricTest {

    private SignedBlockDao blockDao;
    private AppDatabase mDb;

    @Before
    public void setUp() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        blockDao = mDb.blockDao();
    }

    @After
    public void tearDown() throws Exception {
        mDb.close();
    }

    @Test
    public void byteHandingTest() {
        String time = AuthGeneralUtil.getTimestamp();
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

    @Test
    public void writeBlockAndReadInList() throws Exception {
        SignedBlock block = new SignedBlock();
        block.setBlockHeight("1");
        block.setChainId("R0VOVEVTVDE=");

        blockDao.insertAll(block);

        SignedBlock searchedBlock = blockDao.findByPrimaryKey("R0VOVEVTVDE=", "1");
        //assertThat(searchedBlock, is(block));
    }
}