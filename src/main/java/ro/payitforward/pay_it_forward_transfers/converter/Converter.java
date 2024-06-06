package ro.payitforward.pay_it_forward_transfers.converter;

public interface Converter<S, T> {
    T convert (S s);
}
