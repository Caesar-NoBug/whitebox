package org.caesar.article;

public class Test {
    public static void main(String[] args) {
        System.out.println(canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3}));
    }

    public static int canCompleteCircuit(int[] gas, int[] cost) {

        int n = gas.length;
        // 剩余油量
        int g = gas[0];
        // 起点
        int i = 0;
        // 一共走过多少加油站
        int cnt = 1;

        while(true) {

            int j = i + cnt;
            int k = (j - 1) % n;
            if(cost[k] <= g) {

                //  能到达
                g -= cost[k];
                g += gas[j % n];
                cnt ++;
                if(cnt > n) break;
            }
            else {
                //  不能到达
                i = j;

                if(i > n) {
                    i = -1; break;
                }

                g = gas[i];
                cnt = 1;
            }

        }

        return i;
    }
}
