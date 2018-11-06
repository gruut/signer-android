package com.gruutnetworks.gruutsigner.ui.main;

import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
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

public class MainViewModel extends ViewModel {

    private static final String TAG = "MainViewModel";

    public void onClickButton() {
        new GrpcTask()
                .execute(
                        "10.10.10.106",
                        "Hello? ",
                        "50051");
    }


    private static class GrpcTask extends AsyncTask<String, Void, String> {
        private ManagedChannel channel;

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
        }
    }
}
