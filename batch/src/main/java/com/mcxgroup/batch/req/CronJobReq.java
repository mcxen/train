package com.mcxgroup.batch.req;

/**
 * @ClassName CronJobReq
 * @Description
 * @Author McXen@2023/10/24
 **/
public class CronJobReq {
    private String group;

    private String name;

    private String description;

    private String cronExpression;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CronJobReq{");
        sb.append("group='").append(group).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", cronExpression='").append(cronExpression).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
