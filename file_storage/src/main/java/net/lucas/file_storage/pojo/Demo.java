package net.lucas.file_storage.pojo;

import androidx.annotation.NonNull;

public class Demo {

    private int id;

    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "Bean对象打印：Demo{" +
                "id=" + id +
                ", content='" + content + '\'' +
                '}';
    }

}
