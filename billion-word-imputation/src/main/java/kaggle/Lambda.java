package kaggle;

/**
 * Created by szelenin on 12/17/2014.
 */
public interface Lambda<T> {
    void invoke(T param);
}
