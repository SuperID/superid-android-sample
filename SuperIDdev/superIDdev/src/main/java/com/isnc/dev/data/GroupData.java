package com.isnc.dev.data;

import java.util.List;

/**
 * Created by liangjie on 16/7/11.
 * Description:TODO
 */
public class GroupData {

    /**
     * status : 200
     * message : success
     * data : {"groups":[{"id":"6c8185c616f1e9c0536c00448f37c88e","name":"sd,s"},{"id":"ef16cabf83c1f1fa6ca46b461ea6fd78","name":"sds"},{"id":"2bd55330c975005f5d849b632ff9ce59","name":"Test"}]}
     */

    private int status;
    private String message;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 6c8185c616f1e9c0536c00448f37c88e
         * name : sd,s
         */

        private List<GroupsBean> groups;

        public List<GroupsBean> getGroups() {
            return groups;
        }

        public void setGroups(List<GroupsBean> groups) {
            this.groups = groups;
        }

        public static class GroupsBean {
            private String id;
            private String name;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
