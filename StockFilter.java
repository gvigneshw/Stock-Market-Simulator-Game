// Generic interface for filtering stock objects based on specific conditions
public interface StockFilter<T> {
    boolean meetsCriteria(T item);
    String getFilterDescription();
}
