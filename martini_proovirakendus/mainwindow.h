#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>
#include "dbscan.h"

namespace Ui {
    class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

private:
    Ui::MainWindow *ui;
    void updateResults(clusterSet &clusters);

private slots:
    void runDBSCAN();
    void savePointData();
    void loadPointData();
};

#endif // MAINWINDOW_H
