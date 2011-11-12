#ifndef DATAPOINT_H
#define DATAPOINT_H
#include <string>
#include <ostream>
#include <fstream>
using std::ostream;
using std::ifstream;

class Datapoint
{
public:
    friend ostream& operator <<(ostream& os, const Datapoint &p);
    Datapoint();
    Datapoint(const int& px, const int& py):
        x(px),
        y(py)
    {}
    double distance(const Datapoint& point);

    int x;
    int y;
private:
};

#endif // DATAPOINT_H
