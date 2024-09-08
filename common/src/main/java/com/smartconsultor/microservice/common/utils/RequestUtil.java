package com.smartconsultor.microservice.common.utils;

import io.vertx.ext.web.RoutingContext;
import io.vertx.core.http.HttpServerRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

public class RequestUtil {

    // Danh sách các subnet nội bộ
    private static final List<String> INTERNAL_SUBNETS = Arrays.asList(
        "10.0.0.0/8",      // Class A private network
        "192.168.0.0/16",  // Class C private network
        "172.16.0.0/12",   // Class B private network
        "127.0.0.0/8"      // Loopback address
    );

    // Danh sách các IP cụ thể được phép (whitelist)
    private static final List<String> WHITELISTED_IPS = Arrays.asList(
        "192.168.49.2"  // Thêm các IP cụ thể mà bạn tin cậy
    );

    // Phương thức tĩnh để kiểm tra IP nội bộ với bảo mật nâng cao
    public static boolean isInternalIp(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        String ipAddress = request.remoteAddress().host(); // Lấy địa chỉ IP từ remoteAddress trực tiếp

        // Kiểm tra tính hợp lệ của IP và có thuộc subnet nội bộ hoặc trong whitelist không
        if (!isValidIp(ipAddress)) {
            return false; // Địa chỉ IP không hợp lệ
        }

        // Kiểm tra xem địa chỉ IP có trong danh sách whitelist không
        if (WHITELISTED_IPS.contains(ipAddress)) {
            return true;
        }

        // Kiểm tra xem địa chỉ IP có thuộc các subnet nội bộ không
        return isIpInInternalSubnets(ipAddress);
    }

    // Phương thức kiểm tra tính hợp lệ của địa chỉ IP
    private static boolean isValidIp(String ipAddress) {
        try {
            InetAddress.getByName(ipAddress);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    // Phương thức kiểm tra IP có nằm trong dải subnet nội bộ không
    private static boolean isIpInInternalSubnets(String ipAddress) {
        for (String subnet : INTERNAL_SUBNETS) {
            if (isIpInSubnet(ipAddress, subnet)) {
                return true;
            }
        }
        return false;
    }

    // Kiểm tra IP có thuộc subnet cụ thể không
    private static boolean isIpInSubnet(String ipAddress, String subnet) {
        try {
            String[] parts = subnet.split("/");
            InetAddress inetAddress = InetAddress.getByName(parts[0]);
            int subnetPrefix = Integer.parseInt(parts[1]);

            byte[] addressBytes = InetAddress.getByName(ipAddress).getAddress();
            byte[] subnetBytes = inetAddress.getAddress();

            int byteCount = subnetPrefix / 8;
            int bitCount = subnetPrefix % 8;

            for (int i = 0; i < byteCount; i++) {
                if (addressBytes[i] != subnetBytes[i]) {
                    return false;
                }
            }

            if (bitCount > 0) {
                int mask = ~((1 << (8 - bitCount)) - 1);
                if ((addressBytes[byteCount] & mask) != (subnetBytes[byteCount] & mask)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Bạn có thể thêm các phương thức khác như xác thực mTLS hoặc kiểm tra IP tại network layer ở đây
}
