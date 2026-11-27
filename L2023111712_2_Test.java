import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * 测试设计：采用等价类划分与边界值分析，覆盖无重复、重复字母、
 * 可弹栈与不可弹栈、a/z 字符边界以及 1/10000 长度边界。
 * 另外枚举 abc 上长度 1 到 7 的全部字符串，以穷举子序列作为独立预言，
 * 同时验证每个字母出现一次、相对顺序不变和字典序最小。
 * 使用 JUnit 4 运行，不使用自定义反射测试运行器。
 */
public class L2023111712_2_Test {
    private final Solution2 solution = new Solution2();

    /** 目的：最小长度和字符下界；用例 a -> a。 */
    @Test public void singleLetter() {
        assertEquals("a", solution.removeDuplicateLetters("a"));
    }

    /** 目的：字符上界不能越界；用例 z -> z，zzzz -> z。 */
    @Test public void lastAlphabetLetter() {
        assertEquals("z", solution.removeDuplicateLetters("z"));
        assertEquals("z", solution.removeDuplicateLetters("zzzz"));
    }

    /** 目的：单一重复字符只保留一次；用例 aaaa -> a。 */
    @Test public void repeatedLetter() {
        assertEquals("a", solution.removeDuplicateLetters("aaaa"));
    }

    /** 目的：题目示例 1，可弹出后面还会出现的字符；bcabc -> abc。 */
    @Test public void firstExample() {
        assertEquals("abc", solution.removeDuplicateLetters("bcabc"));
    }

    /** 目的：题目示例 2，综合重复与顺序约束；cbacdcbc -> acdb。 */
    @Test public void secondExample() {
        assertEquals("acdb", solution.removeDuplicateLetters("cbacdcbc"));
    }

    /** 目的：无重复字符时不能直接排序；cba -> cba，bca -> bca。 */
    @Test public void preserveRelativeOrder() {
        assertEquals("cba", solution.removeDuplicateLetters("cba"));
        assertEquals("bca", solution.removeDuplicateLetters("bca"));
    }

    /** 目的：已消费的重复字符不能被当成未来机会；bbcaac -> bac。 */
    @Test public void exhaustedOccurrences() {
        assertEquals("bac", solution.removeDuplicateLetters("bbcaac"));
    }

    /** 目的：弹栈后可以重新加入，且跳过已选字符；abacb -> abc。 */
    @Test public void reinsertAndSkip() {
        assertEquals("abc", solution.removeDuplicateLetters("abacb"));
        assertEquals("abc", solution.removeDuplicateLetters("cbacbacba"));
    }

    /** 目的：覆盖全部 26 个字母和连续弹栈；逆序字母表重复两遍。 */
    @Test public void fullAlphabet() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String reverse = new StringBuilder(alphabet).reverse().toString();
        assertEquals(alphabet, solution.removeDuplicateLetters(alphabet));
        assertEquals("azyxwvutsrqponmlkjihgfedcb",
                solution.removeDuplicateLetters(reverse + reverse));
    }

    /** 目的：长度上界与多次调用相互独立；10000 个 z，以及 a。 */
    @Test public void maximumLengthAndRepeatedCalls() {
        StringBuilder input = new StringBuilder();
        for (int i = 0; i < 10000; i++) input.append('z');
        assertEquals("z", solution.removeDuplicateLetters(input.toString()));
        assertEquals("a", solution.removeDuplicateLetters("a"));
    }

    /** 目的：独立穷举交叉验证；abc 上长度 1..7，共 3279 个输入。 */
    @Test public void exhaustiveSmallInputs() {
        for (int length = 1, count = 3; length <= 7; length++, count *= 3) {
            for (int number = 0; number < count; number++) {
                int value = number;
                StringBuilder input = new StringBuilder();
                for (int i = 0; i < length; i++, value /= 3)
                    input.append((char) ('a' + value % 3));
                String s = input.toString();
                assertEquals("input=" + s, bruteForce(s),
                        solution.removeDuplicateLetters(s));
            }
        }
    }

    // 独立预言：枚举子序列，筛选恰好包含全部不同字母的候选，再取最小值。
    private String bruteForce(String s) {
        int required = 0;
        for (char ch : s.toCharArray()) required |= 1 << (ch - 'a');
        String best = null;
        for (int mask = 1; mask < (1 << s.length()); mask++) {
            int seen = 0;
            boolean duplicate = false;
            StringBuilder candidate = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if ((mask & (1 << i)) == 0) continue;
                char ch = s.charAt(i);
                int bit = 1 << (ch - 'a');
                if ((seen & bit) != 0) { duplicate = true; break; }
                seen |= bit;
                candidate.append(ch);
            }
            if (!duplicate && seen == required) {
                String current = candidate.toString();
                if (best == null || current.compareTo(best) < 0) best = current;
            }
        }
        return best;
    }
}
