package task;

public class TaskHash extends TaskImpl {
    private int getHash_value(String S) {
        int p = 31, m = 1000000007;
        int hash_so_far = 0;
        final char[] s = S.toCharArray();
        long p_pow = 1;
        final int n = s.length;
        for (int i = 0; i < n; i++) {
            hash_so_far = (int) ((hash_so_far
                    + (s[i] - 'a' + 1) * p_pow)
                    % m);
            p_pow = (p_pow * p) % m;
        }
        return hash_so_far;
    }

    @Override
    public void execute() {
        result = "The Hashed value of " + input + " = " + getHash_value(input);
    }
}
