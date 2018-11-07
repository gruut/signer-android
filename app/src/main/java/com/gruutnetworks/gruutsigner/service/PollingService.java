package com.gruutnetworks.gruutsigner.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import com.gruutnetworks.gruutsigner.GreeterGrpc;
import com.gruutnetworks.gruutsigner.HelloReply;
import com.gruutnetworks.gruutsigner.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

public class PollingService extends JobService {

    private static final String TAG = "PollingService";

    public PollingService() {
    }

    private Handler handler = new Handler();
    private Runnable handlerTask = new Runnable() {
        @Override
        public void run() {
            new GrpcTask().execute("10.10.10.106", "Hello?", "50051");
            handler.postDelayed(this, 1000 * 5);
        }
    };

    @Override
    public boolean onStartJob(JobParameters params) {

        Log.d(TAG, "onStartJob()");
        handlerTask.run();

        //handler.postDelayed(() -> jobFinished(params, true), 1000 * 60 * 15);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob()");
        handler.removeCallbacks(handlerTask);

        // Retry 여부 return
        return true;
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
