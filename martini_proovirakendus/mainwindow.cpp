#include "mainwindow.h"
#include "ui_mainwindow.h"
#include "renderarea.h"
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <QFileDialog>
using std::cout;
using std::endl;
using std::ofstream;
using std::ifstream;
using std::ios;
using std::string;
using std::istringstream;

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    //Create menus
    QMenu *fileMenu = menuBar()->addMenu("File");
    QAction *loadfile = new QAction("Load file", this);
    connect(loadfile, SIGNAL(triggered()), this, SLOT(loadPointData()));
    fileMenu->addAction(loadfile);

    QAction *savefile = new QAction("Save points", this);
    connect(savefile, SIGNAL(triggered()), this, SLOT(savePointData()));
    fileMenu->addAction(savefile);

    QAction *clear = new QAction("Clear", this);
    connect(clear, SIGNAL(triggered()), ui->renderArea, SLOT(clear()));
    menuBar()->addAction(clear);

    connect(ui->clusterButton, SIGNAL(clicked()), this, SLOT(runDBSCAN()));
    connect(ui->renderArea, SIGNAL(mouseMoved(QString)), this->statusBar(), SLOT(showMessage(QString)));
    connect(ui->renderArea, SIGNAL(pointsChanged(int)), ui->lcdNumber, SLOT(display(int)));
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::runDBSCAN()
{
    double eps = ui->eps_textbox->text().toDouble();
    int minPts = ui->minpts_textbox->text().toInt();
    DBSCAN algorithm(ui->renderArea->getPointData(), eps, minPts);
    clusterSet clusters = algorithm.runDBSCAN();
    updateResults(clusters);
    ui->renderArea->renderCluster(clusters);
}

void MainWindow::updateResults(clusterSet &clusters)
{
    ui->clusters->setText(QString::number(clusters.size()));
    ui->clusterlist->clear();
    QStringList cls;
    for(unsigned int i = 0; i<clusters.size() - 1; i++){
        cls.append("cluster (" + QString::number(clusters[i].size()) + ")");
    }
    cls.append("noise (" + QString::number(clusters.back().size())+ ")");
    ui->clusterlist->insertItems(0, cls);
}

void MainWindow::loadPointData()
{
    QString filename = QFileDialog::getOpenFileName();
    ifstream infile(filename.toAscii(), ios::in);
    ui->renderArea->clear();
    vector<Datapoint> *points = ui->renderArea->getPointData();
    string line;

    int x = 0;
    int y = 0;
    while(getline(infile, line)){
        istringstream linee(line);
        linee >> x >> y;
        points->push_back(Datapoint(x, y));
    }
    infile.close();
    ui->renderArea->update();
}

void MainWindow::savePointData()
{
    vector<Datapoint> *points = ui->renderArea->getPointData();
    if(!points->empty()){
        QString filename = QFileDialog::getSaveFileName();
        ofstream outfile(filename.toAscii(), ios::out);
        for(int i = 0; i < points->size();i++)
            outfile << (*points)[i] << endl;
        outfile.close();
    }
}
