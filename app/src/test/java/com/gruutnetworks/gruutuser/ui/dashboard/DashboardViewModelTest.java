package com.gruutnetworks.gruutuser.ui.dashboard;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.util.Base64;
import androidx.test.core.app.ApplicationProvider;
import com.gruutnetworks.gruutuser.RobolectricTest;
import com.gruutnetworks.gruutuser.model.SignedBlock;
import com.gruutnetworks.gruutuser.model.SignedBlockDao;
import com.gruutnetworks.gruutuser.util.AppDatabase;
import com.gruutnetworks.gruutuser.util.AuthGeneralUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@PrepareForTest({Base64.class})
public class DashboardViewModelTest extends RobolectricTest {

    private SignedBlockDao blockDao;
    private AppDatabase mDb;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        mDb = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        blockDao = mDb.blockDao();
    }

    @After
    public void tearDown() {
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
    public void writeBlockAndReadInList() {
        SignedBlock block = new SignedBlock();
        block.setBlockHeight("1");
        block.setChainId("R0VOVEVTVDE=");

        blockDao.insertAll(block);

        SignedBlock searchedBlock = blockDao.findByPrimaryKey("R0VOVEVTVDE=", "1");
        assertThat(searchedBlock.getChainId(), is(block.getChainId()));
        assertThat(searchedBlock.getBlockHeight(), is(block.getBlockHeight()));

        try {
            blockDao.insertAll(block);
        } catch (SQLiteConstraintException e) {
            // expected
        }
    }
}