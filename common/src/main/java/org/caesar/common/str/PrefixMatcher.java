package org.caesar.common.str;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//使用Trie实现前缀匹配（支持模糊匹配）
public class PrefixMatcher {

    private TreeNode head = new TreeNode();

    public PrefixMatcher(String... prefixes) {
        for (String prefix : prefixes) {
            addPrefix(prefix);
        }
    }

    public PrefixMatcher(List<String> prefixes) {
        for (String prefix : prefixes) {
            addPrefix(prefix);
        }
    }

    public void addPrefix(String prefix) {

        if (StrUtil.isBlank(prefix)) return;

        TreeNode current = head;
        char c = '*';

        for (int i = 0; i < prefix.length(); i++) {

            c = prefix.charAt(i);

            if(current.next(c) == null) current.setNext(c);

            current = current.next(c);

            if(i < prefix.length() - 1 && prefix.charAt(i + 1) == '*') {
                current.setPrefixEnd(true);
                return;
            }
        }

        current.setEnd(true);

    }

    public boolean match(String string) {

        TreeNode current = head;
        char c = '*';

        for (int i = 0; i < string.length(); i++) {

            if(current.isPrefixEnd) return true;

            c = string.charAt(i);
            if(current.next(c) == null) return false;

            current = current.next(c);
        }

        return current.isEnd || current.isPrefixEnd;
    }

    /*public static void main(String[] args) {
        PrefixMatcher prefixMatcher = new PrefixMatcher("abc", "acd", "abd*");
        System.out.println(prefixMatcher.match("ab"));
        System.out.println(prefixMatcher.match("abc"));
        System.out.println(prefixMatcher.match("abcd"));
        System.out.println(prefixMatcher.match("abd"));
        System.out.println(prefixMatcher.match("abdf"));
    }*/

    /**
     * nodeMap:     下一个节点的链接
     * isEnd:       结尾（精确匹配，即目标以当前节点为结尾）
     * isPrefixEnd: 前缀结尾（模糊匹配，即目标只要包含此前缀即可）
     */
    @Data
    private static class TreeNode {
        private final Map<Character, TreeNode> nodeMap = new HashMap<>();
        private boolean isEnd = false;
        private boolean isPrefixEnd = false;

        public TreeNode next(char c) {
            return nodeMap.get(c);
        }

        public void setNext(char c) {
            TreeNode node = new TreeNode();
            nodeMap.put(c, node);
        }

    }

}
