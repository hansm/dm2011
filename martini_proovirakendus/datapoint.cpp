#include "datapoint.h"
#include <cmath>
#include <sstream>
using std::ostringstream;

Datapoint::Datapoint()
{
}

double Datapoint::distance(const Datapoint &point)
{
    return sqrt(pow(x-point.x, 2)+pow(y-point.y,2));
}

ostream& operator <<(ostream &os, const Datapoint &p)
{
    os << p.x << " " << p.y;
    return os;
}
