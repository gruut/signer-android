package com.gruutnetworks.gruutsigner.ui.main;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.Arrays;

import static com.gruutnetworks.gruutsigner.util.CompressionUtil.compress;
import static com.gruutnetworks.gruutsigner.util.CompressionUtil.decompress;

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    public void onClickButton() {
        try {
            String str = "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세\n";
            str += "무궁화 삼천리 화려강산 대한사람 대한으로 길이보전하세";

            byte[] testArr = str.getBytes("UTF-8");
            byte[] compressed = compress(testArr);
            Log.d(TAG, "compressed: " + Arrays.toString(compressed));

            byte[] decompressed = decompress(compressed);
            Log.d(TAG, "decompressed: " + new String(decompressed));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }
}
