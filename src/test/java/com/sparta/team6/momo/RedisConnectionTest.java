//package sparta.team6.momo;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//public class RedisConnectionTest {
//
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    @Test
//    void testRedisString() {
//        // given
//        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();;
//        String k = "key";
//
//        // when
//        valueOperations.set(k, "hello world");
//
//        // then
//        String v = valueOperations.get(k);
//        assertThat(v).isEqualTo("hello world");
//
//    }
//}
