#include "dbscan.h"
#include <iostream>
using std::cout;
using std::endl;

DBSCAN::DBSCAN(vector<Datapoint> *data, double eps, int minPts)
{
    visited = new short[data->size()];
    this->eps = eps;
    this->minPts = minPts;
    this->dataset = data;

    for(unsigned int i = 0; i< data->size(); i++){
        visited[i] = 0;
    }
    createDistanceMatrix();
}

DBSCAN::~DBSCAN()
{
    delete[] visited;
    for(int i = dataset->size()-1; i>0; i--){
        delete[] distanceMatrix[i];
    }
    delete[] distanceMatrix;
}

clusterSet DBSCAN::runDBSCAN()
{
    cout << "eps: " << this->eps << endl;
    cout << "minPts: " <<this->minPts << endl;
    vector<int> N;
    vector<int> N_prim;
    int p_prim;

    for(unsigned int i=0;i<dataset->size();i++){
        if(visited[i]!=0)
            continue;

        visited[i] = 1;
        fillNeighborIndexes(N, i);
        if(N.size() < minPts)
            visited[i] = -1;
        else{
            clusters.push_back(cluster());
            clusters.back().push_back(i);
            visited[i] = 2;
            for(unsigned int k = 0;k<N.size();k++){
                p_prim = N[k];
                if(visited[p_prim] == 0){
                    visited[p_prim] = 1;
                    fillNeighborIndexes(N_prim, p_prim);
                    if(N_prim.size() >= minPts)
                        for(unsigned int j = 0; j<N_prim.size();j++)
                            N.push_back(N_prim[j]);

                    if(p_prim != 2)
                        clusters.back().push_back(p_prim);
                }

            }
        }
    }
//    cout << "Dataset size: " << dataset->size() << endl;
//    cout << "Nr clusters: " << clusters.size() << endl;
//    for(int i=0; i< clusters.size(); i++)
//        cout << "Cluster "<< i+1 << " size: " << clusters[i].size() << endl;
//    int noise = 0;
//    int unvisited = 0;
//    for(int i=0; i< dataset->size();i++){
//        if(visited[i]==-1)
//            noise++;
//        if(visited[i]==0)
//            unvisited++;
//    }
//    cout << "noise: " << noise << endl;
//    cout << "unvisited: " << unvisited << endl;

    //Lets add the noise as the last cluster
    clusters.push_back(cluster());
    for(unsigned int i=0; i< dataset->size();i++)
        if(visited[i]==-1)
            clusters.back().push_back(i);
    return clusters;
}

void DBSCAN::fillNeighborIndexes(vector<int> &N, int datapoint)
{
    N.clear();
    for(unsigned int i=0;i<dataset->size();i++)
        if(i != datapoint && distanceMatrix[datapoint][i] < eps)
            N.push_back(i);
}

void DBSCAN::createDistanceMatrix()
{
    distanceMatrix = new double*[dataset->size()];
    for(int i=0;i<dataset->size();i++)
        distanceMatrix[i] = new double[dataset->size()];

    for(unsigned int i=0; i<dataset->size();i++){
        for(unsigned int j=0;j<dataset->size();j++){
            if(i == j)
                distanceMatrix[i][j] = 0.0;
            else
                distanceMatrix[i][j] = dataset->at(i).distance(dataset->at(j));
        }
    }

}
