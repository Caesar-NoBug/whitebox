package org.caesar.common.util;

import org.caesar.common.vo.Expression;

import java.util.Random;

public class ExpressionUtil {

    /**
     * 生成一个随机的表达式，用于生成验证码
     * @param numCount 表达式中数字的个数
     * @param numBound 表达式中数字的范围
     * @return 随机表达式及结果
     */
    public static Expression genExpression(int numCount, int numBound) {

        Random random = new Random();

        int[] nums = new int[numCount];

        for (int i = 0; i < numCount; i++) {
            nums[i] = random.nextInt(numBound);
        }

        //决定运算符是什么,[0, 0.5)之间时为加减，[0.5, 1)之间时为乘除
        double op = random.nextDouble();

        //表达式
        StringBuilder exp = new StringBuilder();
        exp.append(nums[0]);
        //结果
        int res = nums[0];

        for (int i = 0; i < numCount - 1; i++) {

            int next = nums[i + 1];
            //有50%的概率选择乘除
            if(op > 0.5) {
                //只有可以整除时才选择除法,否则选择乘法
                if(next != 0 && res % next == 0) {
                    res = res / next;
                    exp.append('/');
                }
                else {
                    res = res * next;
                    exp.append('*');
                }
                //重新生成运算符，只有当前为乘除时下一个运算符才能是乘除，否则无法保证直接从左往右计算时结果是正确的
                op = random.nextDouble();
            }
            else {
                if(op < 0.25) {
                    res = res + next;
                    exp.append('+');
                }
                else {
                    res = res - next;
                    exp.append('-');
                }
                //重新生成运算符，且只能生成加减运算符
                op = random.nextDouble() / 2;
            }

            exp.append(next);
        }

        exp.append("=?");

        return new Expression(exp.toString(), res);
    }

    public static void main(String[] args) {
        System.out.println(genExpression(4, 10));
    }

}
