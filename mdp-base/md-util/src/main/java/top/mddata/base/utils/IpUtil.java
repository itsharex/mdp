package top.mddata.base.utils;

/**
 * IP匹配工具类，支持三种模式：
 * 1. 精确IP:   192.168.1.10
 * 2. 通配模糊: 192.168.* / 192.*.* / *.*.*.*（* 通配该段，段数不足时尾部补 *）
 * 3. CIDR网段: 192.168.1.0/24 / 0.0.0.0/0
 *
 * @author henhen6
 * @since 2026/6/23
 */
public final class IpUtil {
    private IpUtil() {
    }

    /**
     * IP匹配，自动识别模式：
     * 含 "/" 走CIDR匹配，含 "*" 走通配符匹配，否则走精确匹配
     */
    public static boolean matchIp(String pattern, String ip) {
        if (pattern == null || ip == null) {
            return false;
        }
        if (pattern.contains("/")) {
            return matchCidr(pattern, ip);
        }
        if (pattern.contains("*")) {
            return matchWildcard(pattern, ip);
        }
        return pattern.equals(ip);
    }

    /** CIDR网段匹配，如 192.168.1.0/24 */
    public static boolean matchCidr(String cidr, String ip) {
        String[] parts = cidr.split("/");
        if (parts.length != 2) {
            return false;
        }
        int prefixLen;
        try {
            prefixLen = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return false;
        }
        if (prefixLen < 0 || prefixLen > 32) {
            return false;
        }
        long ipLong = ipToLong(ip);
        long networkLong = ipToLong(parts[0]);
        if (ipLong < 0 || networkLong < 0) {
            return false;
        }
        long mask = prefixLen == 0 ? 0 : (0xFFFFFFFFL << (32 - prefixLen));
        return (ipLong & mask) == (networkLong & mask);
    }

    /** 通配符模糊匹配，支持段数不足（如 192.168.* 等同 192.168.*.*） */
    public static boolean matchWildcard(String pattern, String ip) {
        String[] patternParts = pattern.split("\\.");
        String[] ipParts = ip.split("\\.");
        if (ipParts.length != 4) {
            return false;
        }
        String[] fullPattern = new String[]{"*", "*", "*", "*"};
        for (int i = 0; i < patternParts.length && i < 4; i++) {
            fullPattern[i] = patternParts[i];
        }
        for (int i = 0; i < 4; i++) {
            if ("*".equals(fullPattern[i])) {
                continue;
            }
            if (!fullPattern[i].equals(ipParts[i])) {
                return false;
            }
        }
        return true;
    }

    /** 将IPv6回环地址归一化为IPv4回环地址 */
    public static String normalizeLoopback(String ip) {
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }

    /** IPv4地址转long，格式不合法返回-1 */
    public static long ipToLong(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return -1;
        }
        long result = 0;
        for (int i = 0; i < 4; i++) {
            try {
                int octet = Integer.parseInt(parts[i]);
                if (octet < 0 || octet > 255) {
                    return -1;
                }
                result = (result << 8) | octet;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return result;
    }
}
