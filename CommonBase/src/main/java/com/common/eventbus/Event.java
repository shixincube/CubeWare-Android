package com.common.eventbus;

/**
 * EventBus的事件模型类
 *
 * @author LiuFeng
 * @data 2020/1/9 11:35
 */
public class Event<T> {
    public String eventName;
    public T      data;

    public Event(String eventName) {
        this.eventName = eventName;
    }

    public Event(String eventName, T data) {
        this.eventName = eventName;
        this.data = data;
    }
}
