#ifndef RENDERAREA_H
#define RENDERAREA_H

#include <QWidget>
#include <vector>
#include "datapoint.h"
#include "dbscan.h"
using std::vector;

class RenderArea : public QWidget
{
    Q_OBJECT
public:
    explicit RenderArea(QWidget *parent = 0);
    vector <Datapoint>* getPointData() { return &points; }
    void renderCluster(const clusterSet &clusters);

signals:
    void mouseMoved(QString);
    void pointsChanged(int);

protected:
    void paintEvent(QPaintEvent *);
    void mousePressEvent(QMouseEvent *event);
    void mouseMoveEvent(QMouseEvent *event);

public slots:
    void clear();

private:
    vector <Datapoint> points;
    vector <QColor> clusterColors;
    QColor noisecolor;
    clusterSet clusters;
};

#endif // RENDERAREA_H
