package com.gruutnetworks.gruutsigner.service;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import com.evernote.android.job.Job;
import com.gruutnetworks.gruutsigner.GreeterGrpc;
import com.gruutnetworks.gruutsigner.HelloReply;
import com.gruutnetworks.gruutsigner.HelloRequest;
import com.gruutnetworks.gruutsigner.util.NetworkUtil;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

public class PollingSyncJob extends Job {

    public static final String TAG = "job_pooling_tag";
    private boolean finishFlag = false;

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {

        Log.d(TAG, "onRunJob()");

        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(1000 * 60 * 15);
                finishFlag = true;
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (!finishFlag) {
                    if (NetworkUtil.isConnected(getContext())) {
                        new GrpcTask().execute("10.10.10.106", "Hello?", "50051");
                    } else {
                        Log.e(TAG, "Unable to use the network.");
                    }
                    SystemClock.sleep(1000 * 5);
                }
            }
        }.start();


        return Result.SUCCESS;
    }

    private static class GrpcTask extends AsyncTask<String, Void, String> {
        private ManagedChannel channel;

        private long start;

        private GrpcTask() {
        }

        @Override
        protected String doInBackground(String... params) {
            String host = params[0];
            String message = params[1];
            String portStr = params[2];
            int port = TextUtils.isEmpty(portStr) ? 0 : Integer.valueOf(portStr);
            try {
                channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                GreeterGrpc.GreeterBlockingStub stub = GreeterGrpc.newBlockingStub(channel);
                HelloRequest request = HelloRequest.newBuilder().setName(message).build();
                HelloReply reply = stub.sayHello(request);

                start = System.currentTimeMillis();
                return reply.getMessage();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return String.format("Failed... : %n%s", sw);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Log.d(TAG, "Result: " + result);
            Log.d(TAG, "Response Time: " + (System.currentTimeMillis() - start));
        }
    }
}
