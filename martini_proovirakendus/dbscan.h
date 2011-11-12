#ifndef DBSCAN_H
#define DBSCAN_H
#include <vector>
#include "datapoint.h"
using std::vector;

typedef vector<int> cluster;
typedef vector<cluster> clusterSet;
class DBSCAN
{
public:
    DBSCAN(vector <Datapoint> *data, double eps, int minPts );
    ~DBSCAN();

    clusterSet runDBSCAN();
private:
    clusterSet clusters;
    vector <Datapoint> *dataset;
    unsigned int minPts;
    double eps;
    short *visited;
    double **distanceMatrix;

    void createDistanceMatrix();
    void fillNeighborIndexes(vector<int> &N, int datapoint);

};

#endif // DBSCAN_H
