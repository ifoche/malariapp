/**
Copyright (c) 2013-2015 Nick Downie
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
**/

/*
    Usage:

    build({
        title:'Sample tabgroup',
        tip:'Quality of care(based on last assessment)',
        idTabGroup: 1,
        valueA:10,
        valueB:20,
        valueC:0
    })
*/

class ScoringPieChartBuilder{
    build(data){

        var canvasDOMId = "tabgroupCanvas";
        var legendDOMId = "tabgroupLegend";
        var titleTableDOMId = "tabgroupTip";

        //Chart
        var ctx = document.getElementById(canvasDOMId).getContext("2d");
        var myChart = new Chart(ctx).Pie(
            [{
                value: data.valueA,
                color: classificationContext.colors.a,
                label: "A (>" + classificationContext.scores.high + ")"
            }, {
                value: data.valueB,
                color: classificationContext.colors.b,
                label: "B (" + classificationContext.scores.mediumFormatted + ")"
            }, {
                value: data.valueC,
                color: classificationContext.colors.c,
                label: "C (<" + classificationContext.scores.low + ")"
            }],
            {
                tooltipTemplate: "<%= value %>",
                onAnimationComplete: function () {
                    this.showTooltip(this.segments, true);
                },
                tooltipEvents: [],
                showTooltips: true
            }
        );
        //Legend
        document.getElementById(legendDOMId).insertAdjacentHTML("beforeend", myChart.generateLegend());

        //Update title && tip
        updateChartTitle(titleTableDOMId, data.tip);
    }
}
