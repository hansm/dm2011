#include "renderarea.h"
#include <QtGui>
#include <iostream>
using std::cout;
using std::endl;

RenderArea::RenderArea(QWidget *parent) :
    QWidget(parent)
{
    clusterColors.push_back(QColor(227,20,20));
    clusterColors.push_back(QColor(132,20,227));
    clusterColors.push_back(QColor(43,232,29));
    clusterColors.push_back(QColor(83,220,183));
    clusterColors.push_back(QColor(226,20,234));
    clusterColors.push_back(QColor(226,20,234));
    noisecolor = QColor(230,230,230);
    this->setMouseTracking(true);
}

void RenderArea::paintEvent(QPaintEvent *)
{
    emit pointsChanged(points.size());
    QPainter painter(this);
    painter.fillRect(0, 0, width(), height(), Qt::white);
    QPen pen;
    pen.setWidth(3);
    painter.setPen(pen);
    if(this->clusters.size() == 0)
        for(int i = 0; i< points.size(); i++){
            painter.drawPoint(points[i].x, points[i].y);
        }
    else {
        for(int i = 0; i< clusters.size()-1;i++){
            pen.setColor(clusterColors[i%clusterColors.size()]);
            painter.setPen(pen);
            for(int j =0; j< clusters[i].size();j++)
                painter.drawPoint(points[clusters[i][j]].x, points[clusters[i][j]].y);
        }

        pen.setColor(noisecolor);
        painter.setPen(pen);
        for(int i=0;i<clusters.back().size(); i++)
            painter.drawPoint(points[clusters.back()[i]].x, points[clusters.back()[i]].y);
    }
}

void RenderArea::renderCluster(const clusterSet &clusters)
{
    this->clusters = clusters;
    update();
}

void RenderArea::mousePressEvent(QMouseEvent *event)
{
    clusters.clear();
    points.push_back(Datapoint(event->x(), event->y()));
    update();
}

void RenderArea::mouseMoveEvent(QMouseEvent *event)
{
    QString cords = QString("X: %1 Y: %2").arg(event->x()).arg(event->y());
    emit mouseMoved(cords);
    if (event->buttons() & Qt::LeftButton){
        points.push_back(Datapoint(event->x(), event->y()));
        update();
    }
}

void RenderArea::clear()
{
    clusters.clear();
    points.clear();
    update();
}
