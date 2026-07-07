package androidx.constraintlayout.core.widgets.analyzer;

/* JADX INFO: loaded from: /data/data/com.termux/files/home/BridgeApp/apk_temp/classes.dex */
class BaselineDimensionDependency extends DimensionDependency {
    public BaselineDimensionDependency(WidgetRun run) {
        super(run);
    }

    public void update(DependencyNode node) {
        VerticalWidgetRun verticalRun = (VerticalWidgetRun) this.run;
        verticalRun.baseline.margin = this.run.widget.getBaselineDistance();
        this.resolved = true;
    }
}
