package coop.bancocredicoop.guv.persistor.actors;

import com.fasterxml.jackson.annotation.*;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageType<L, R> implements Serializable {

    private L _left = null;
    private R _right = null;

    public MessageType() {}

    public void setLeft(L _left) {
        this._left = _left;
    }

    public void setRight(R _right) {
        this._right = _right;
    }

    @JsonIgnore
    public boolean isLeft() {
        return !Objects.isNull(this._left);
    }

    @JsonIgnore
    public boolean isRight() {
        return !Objects.isNull(_right);
    }

    @JsonIgnore
    public Optional<L> left() {
        return Optional.ofNullable(this._left);
    }

    @JsonIgnore
    public Optional<R> right() {
        return Optional.ofNullable(this._right);
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "left=" + ObjectUtils.nullSafeToString(_left) +
                ", right=" + ObjectUtils.nullSafeToString(_right) +
                '}';
    }
}
