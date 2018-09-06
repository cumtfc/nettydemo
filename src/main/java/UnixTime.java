import java.util.Date;

/**
 * @author fengchu created on 2018/9/6-17:29
 */
public class UnixTime {

    private final long value;

    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public UnixTime(long value) {
        this.value = value;
    }

    public static UnixTime fromObject(Object o) {
        if (o instanceof UnixTime) {
            return (UnixTime) o;
        } else {
            throw new ClassCastException("cannot cast" + o.getClass().getName() + "to" + UnixTime.class);
        }
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
