package com.blogspot.ostas.lora.nodes.replication.report;

public class ExecutionItem {
    int id;
    int nodesCount;
    long execTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNodesCount() {
        return nodesCount;
    }

    public void setNodesCount(int nodesCount) {
        this.nodesCount = nodesCount;
    }

    public long getExecTime() {
        return execTime;
    }

    public void setExecTime(long execTime) {
        this.execTime = execTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        //sb.append("ExecutionItem");
        sb.append("{id: ").append(id);
        sb.append(", nodesCount: ").append(nodesCount);
        sb.append(", execTime: ").append(execTime);
        sb.append('}');
        return sb.toString();
    }
}
