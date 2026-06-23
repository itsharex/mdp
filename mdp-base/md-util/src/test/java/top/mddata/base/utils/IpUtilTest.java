package top.mddata.base.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("IpUtil IP匹配单元测试")
class IpUtilTest {

    // ======================== matchIp 综合测试 ========================

    @Nested
    @DisplayName("matchIp - 综合分发")
    class MatchIpDispatch {

        @Test
        @DisplayName("pattern或ip为null时返回false")
        void shouldReturnFalseWhenPatternOrIpIsNull() {
            assertFalse(IpUtil.matchIp(null, "192.168.1.1"));
            assertFalse(IpUtil.matchIp("192.168.1.1", null));
            assertFalse(IpUtil.matchIp(null, null));
        }

        @ParameterizedTest
        @DisplayName("精确匹配")
        @CsvSource({
                "192.168.1.10, 192.168.1.10",
                "10.0.0.1, 10.0.0.1",
                "127.0.0.1, 127.0.0.1",
                "0.0.0.0, 0.0.0.0",
                "255.255.255.255, 255.255.255.255",
        })
        void shouldExactMatch(String pattern, String ip) {
            assertTrue(IpUtil.matchIp(pattern, ip));
        }

        @ParameterizedTest
        @DisplayName("精确不匹配")
        @CsvSource({
                "192.168.1.10, 192.168.1.11",
                "10.0.0.1, 10.0.0.2",
                "192.168.1.1, 192.168.2.1",
        })
        void shouldExactNotMatch(String pattern, String ip) {
            assertFalse(IpUtil.matchIp(pattern, ip));
        }

        @Test
        @DisplayName("含 / 时走CIDR分发")
        void shouldDelegateToCidrWhenContainsSlash() {
            assertTrue(IpUtil.matchIp("192.168.1.0/24", "192.168.1.100"));
            assertFalse(IpUtil.matchIp("192.168.1.0/24", "192.168.2.1"));
        }

        @Test
        @DisplayName("含 * 时走通配符分发")
        void shouldDelegateToWildcardWhenContainsStar() {
            assertTrue(IpUtil.matchIp("192.168.*", "192.168.1.100"));
            assertFalse(IpUtil.matchIp("192.168.1.*", "192.168.2.1"));
        }
    }

    // ======================== matchCidr 测试 ========================

    @Nested
    @DisplayName("matchCidr - CIDR网段匹配")
    class MatchCidr {

        @ParameterizedTest
        @DisplayName("CIDR匹配成功")
        @CsvSource({
                "192.168.1.0/24, 192.168.1.1",
                "192.168.1.0/24, 192.168.1.100",
                "192.168.1.0/24, 192.168.1.254",
                "192.168.1.0/24, 192.168.1.0",
                "10.0.0.0/8, 10.1.2.3",
                "10.0.0.0/8, 10.255.255.255",
                "172.16.0.0/12, 172.16.0.1",
                "172.16.0.0/12, 172.31.255.255",
                "192.168.0.0/16, 192.168.1.1",
                "192.168.0.0/16, 192.168.255.255",
                "192.168.1.100/32, 192.168.1.100",
                "0.0.0.0/0, 192.168.1.1",
                "0.0.0.0/0, 10.0.0.1",
                "0.0.0.0/0, 127.0.0.1",
                "0.0.0.0/0, 0.0.0.0",
                "192.168.1.0/24, 192.168.1.255",
                "10.0.0.0/8, 10.0.0.0",
        })
        void shouldMatchCidr(String cidr, String ip) {
            assertTrue(IpUtil.matchCidr(cidr, ip));
        }

        @ParameterizedTest
        @DisplayName("CIDR匹配失败")
        @CsvSource({
                "192.168.1.0/24, 192.168.2.1",
                "192.168.1.0/24, 10.0.0.1",
                "10.0.0.0/8, 11.0.0.1",
                "192.168.1.100/32, 192.168.1.101",
                "172.16.0.0/12, 172.15.255.255",
        })
        void shouldNotMatchCidr(String cidr, String ip) {
            assertFalse(IpUtil.matchCidr(cidr, ip));
        }

        @ParameterizedTest
        @DisplayName("非法CIDR格式返回false")
        @ValueSource(strings = {
                "192.168.1.0",
                "192.168.1.0/",
                "192.168.1.0/abc",
                "192.168.1.0/-1",
                "192.168.1.0/33",
                "not-an-ip/24",
                "192.168.1/24",
                "192.168.1.0/24/extra",
        })
        void shouldReturnFalseForInvalidCidr(String cidr) {
            assertFalse(IpUtil.matchCidr(cidr, "192.168.1.100"));
        }

        @ParameterizedTest
        @DisplayName("非法IP格式返回false")
        @ValueSource(strings = {
                "abc.def.ghi.jkl",
                "192.168.1",
                "192.168.1.1.1",
                "192.168.1.256",
                "192.168.-1.1",
        })
        void shouldReturnFalseForInvalidIp(String ip) {
            assertFalse(IpUtil.matchCidr("192.168.1.0/24", ip));
        }
    }

    // ======================== matchWildcard 测试 ========================

    @Nested
    @DisplayName("matchWildcard - 通配符模糊匹配")
    class MatchWildcard {

        @ParameterizedTest
        @DisplayName("通配符匹配成功")
        @CsvSource({
                "*, 192.168.1.1",
                "*.*.*.*, 10.0.0.1",
                "*.*.*.*, 127.0.0.1",
                "192.*.*.*, 192.1.2.3",
                "192.168.*.*, 192.168.1.100",
                "192.168.1.*, 192.168.1.100",
                "192.168.*.*, 192.168.0.0",
                "192.168.*.*, 192.168.255.255",
                "192.168.*, 192.168.1.100",
                "192.168.*, 192.168.255.255",
                "192.*, 192.1.2.3",
                "192.*, 192.255.255.255",
                "*.168.1.1, 192.168.1.1",
                "192.*.1.1, 192.168.1.1",
        })
        void shouldMatchWildcard(String pattern, String ip) {
            assertTrue(IpUtil.matchWildcard(pattern, ip));
        }

        @ParameterizedTest
        @DisplayName("通配符匹配失败")
        @CsvSource({
                "192.168.*.*, 10.0.0.1",
                "192.168.1.*, 192.168.2.1",
                "10.*.*.*, 192.168.1.1",
                "*.168.1.1, 192.169.1.1",
        })
        void shouldNotMatchWildcard(String pattern, String ip) {
            assertFalse(IpUtil.matchWildcard(pattern, ip));
        }

        @ParameterizedTest
        @DisplayName("非法段数IP返回false")
        @ValueSource(strings = {
                "192.168.1",
                "192.168.1.1.1",
        })
        void shouldReturnFalseForInvalidSegmentCount(String ip) {
            assertFalse(IpUtil.matchWildcard("*.*.*.*", ip));
        }

        @Test
        @DisplayName("pattern无通配符时等价精确匹配")
        void shouldExactMatchWhenNoWildcard() {
            assertTrue(IpUtil.matchWildcard("192.168.1.10", "192.168.1.10"));
            assertFalse(IpUtil.matchWildcard("192.168.1.10", "192.168.1.11"));
        }
    }

    // ======================== ipToLong 测试 ========================

    @Nested
    @DisplayName("ipToLong - IPv4转long")
    class IpToLong {

        @ParameterizedTest
        @DisplayName("合法IP转换")
        @CsvSource({
                "0.0.0.0, 0",
                "192.168.1.1, 3232235777",
                "255.255.255.255, 4294967295",
                "10.0.0.1, 167772161",
                "127.0.0.1, 2130706433",
        })
        void shouldConvertValidIp(String ip, long expected) {
            assertEquals(expected, IpUtil.ipToLong(ip));
        }

        @ParameterizedTest
        @DisplayName("非法IP返回-1")
        @ValueSource(strings = {
                "192.168.1",
                "192.168.1.1.1",
                "abc.def.ghi.jkl",
                "192.168.1.256",
                "192.168.-1.1",
                "",
        })
        void shouldReturnNegativeForInvalidIp(String ip) {
            assertEquals(-1, IpUtil.ipToLong(ip));
        }
    }

    // ======================== matchIp 边界场景 ========================

    @Nested
    @DisplayName("matchIp - 边界场景")
    class MatchIpEdgeCases {

        @Test
        @DisplayName("0.0.0.0精确匹配")
        void shouldExactMatch0000() {
            assertTrue(IpUtil.matchIp("0.0.0.0", "0.0.0.0"));
            assertFalse(IpUtil.matchIp("0.0.0.0", "192.168.1.1"));
        }

        @Test
        @DisplayName("0.0.0.0/0匹配任意IP")
        void shouldMatchAnyIpWithCidrZero() {
            assertTrue(IpUtil.matchIp("0.0.0.0/0", "127.0.0.1"));
            assertTrue(IpUtil.matchIp("0.0.0.0/0", "192.168.1.1"));
            assertTrue(IpUtil.matchIp("0.0.0.0/0", "10.0.0.1"));
            assertTrue(IpUtil.matchIp("0.0.0.0/0", "0.0.0.0"));
            assertTrue(IpUtil.matchIp("0.0.0.0/0", "255.255.255.255"));
        }

        @Test
        @DisplayName("* 匹配任意IP")
        void shouldMatchAnyIpWithStar() {
            assertTrue(IpUtil.matchIp("*", "127.0.0.1"));
            assertTrue(IpUtil.matchIp("*", "192.168.1.1"));
        }

        @Test
        @DisplayName("192.168.* 短模式")
        void shouldMatchShortPattern() {
            assertTrue(IpUtil.matchIp("192.168.*", "192.168.1.100"));
            assertTrue(IpUtil.matchIp("192.168.*", "192.168.0.0"));
            assertFalse(IpUtil.matchIp("192.168.*", "192.169.1.1"));
        }
    }
}
