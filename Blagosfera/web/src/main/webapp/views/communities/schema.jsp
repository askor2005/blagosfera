<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<!------------------------- min ----------------------->
<link type="text/css" rel="stylesheet" href="/js/community/draw2d_GPL_5.2.0-min/css/contextmenu.css" />
<link rel="stylesheet" type="text/css" href="/css/modal-fullscreen.css" />
<link rel="stylesheet" href="/css/bootstrap-colorpicker.min.css">

<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/shifty.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/raphael.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/jquery.autoresize.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/jquery-touch_punch.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/jquery.contextmenu.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/rgbcolor.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/canvg.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/Class.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/json2.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/lib/pathfinding-browser.min.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-min/src/draw2d.js"></SCRIPT>
<SCRIPT src="/js/bootstrap-colorpicker.min.js"></SCRIPT>

<!------------------------- debug ----------------------->
<!-- <SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/draw2d.js"></SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/Polyfill.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/Base64.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/Debug.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/Color.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/ArrayList.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/SVGUtil.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/JSONUtil.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/UUID.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/spline/Spline.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/spline/CubicSpline.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/spline/CatmullRomSpline.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/util/spline/BezierSpline.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/geo/PositionConstants.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/geo/Point.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/geo/Rectangle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/geo/Ray.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandType.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/Command.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandCollection.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandStack.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandStackEvent.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandStackEventListener.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandMove.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandMoveLine.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandMoveVertex.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandMoveVertices.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandResize.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandRotate.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandConnect.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandReconnect.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandDelete.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandAdd.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandGroup.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandUngroup.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandAddVertex.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandAssignFigure.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandBoundingBox.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandRemoveVertex.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/command/CommandReplaceVertices.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/ConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/DirectRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/VertexRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/ManhattanConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/ManhattanBridgedConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/InteractiveManhattanConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/CircuitConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/SplineConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/FanConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/MazeConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/MuteableManhattanConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/connection/SketchConnectionRouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/mesh/MeshLayouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/mesh/ExplodeLayouter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/mesh/ProposedMeshChange.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/Locator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/PortLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/XYAbsPortLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/XYRelPortLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/InputPortLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/OutputPortLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/ConnectionLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/ManhattanMidpointLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/PolylineMidpointLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/ParallelMidpointLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/TopLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/BottomLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/LeftLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/RightLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/locator/CenterLocator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/EditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/CanvasPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/ConnectionInterceptorPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/KeyboardPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/DefaultKeyboardPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/ExtendedKeyboardPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/SelectionPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/SingleSelectionPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/GhostMoveSelectionPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/PanningSelectionPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/BoundingboxSelectionPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/ReadOnlySelectionPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/DecorationPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/FadeoutDecorationPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/CoronaDecorationPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/SnapToEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/SnapToGridEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/ShowGridEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/ShowDotEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/ShowChessboardEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/canvas/SnapToGeometryEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/FigureEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/DragDropEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/RegionEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/HorizontalEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/VerticalEditPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/SelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/ResizeSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/RectangleSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/BigRectangleSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/RoundRectangleSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/BusSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/WidthSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/VBusSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/HBusSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/AntSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/GlowSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/SlimSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/figure/VertexSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/line/LineSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/line/VertexSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/line/OrthogonalSelectionFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/port/PortFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/port/ElasticStrapFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/policy/port/IntrusivePortsFeedbackPolicy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/Configuration.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/Canvas.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/Selection.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/Figure.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/Node.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/VectorFigure.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Rectangle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/SetFigure.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/SVGFigure.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/Hub.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/HorizontalBus.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/VerticalBus.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/Fulcrum.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Arc.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Oval.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Circle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Label.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Text.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Line.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/PolyLine.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Image.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Polygon.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/Diamond.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/composite/Composite.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/composite/StrongComposite.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/composite/Group.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/composite/Jailhouse.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/composite/WeakComposite.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/composite/Raft.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/Connection.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/VectorFigure.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/ResizeHandle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/LineResizeHandle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/LineStartResizeHandle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/LineEndResizeHandle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/VertexResizeHandle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/basic/GhostVertexResizeHandle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/Port.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/InputPort.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/OutputPort.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/HybridPort.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/anchor/ConnectionAnchor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/anchor/ChopboxConnectionAnchor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/anchor/FanConnectionAnchor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/anchor/ShortesPathConnectionAnchor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/layout/anchor/CenterEdgeConnectionAnchor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/arrow/CalligrapherArrowLeft.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/arrow/CalligrapherArrowDownLeft.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/Start.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/End.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/node/Between.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/note/PostIt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/widget/Widget.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/widget/Slider.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/diagram/Diagram.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/diagram/Pie.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/diagram/Sparkline.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/analog/OpAmp.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/analog/ResistorBridge.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/analog/ResistorVertical.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/analog/VoltageSupplyHorizontal.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/analog/VoltageSupplyVertical.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/layout/Layout.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/layout/HorizontalLayout.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/layout/VerticalLayout.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Icon.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Thunder.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Snow.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Hail.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Rain.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Cloudy.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Sun.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Undo.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Detour.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Merge.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Split.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Fork.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ForkAlt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Exchange.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Shuffle.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Refresh.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ccw.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Acw.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Contract.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Expand.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Stop.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/End.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Start.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ff.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Rw.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ArrowRight.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ArrowLeft.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ArrowUp.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ArrowDown.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ArrowLeft2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ArrowRight2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Smile2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Smile.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Alarm.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Clock.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/StopWatch.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/History.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Future.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/GlobeAlt2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/GlobeAlt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Globe.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Warning.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Code.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Pensil.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Pen.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Plus.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Minus.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/TShirt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Sticker.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Page2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Page.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Landscape1.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Landscape2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Plugin.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Bookmark.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Hammer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Users.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/User.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Customer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Employee.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Anonymous.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Skull.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Mail.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Picture.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Bubble.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/CodeTalk.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Talkq.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Talke.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Home.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Lock.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Clip.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Star.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/StarOff.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Star2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Star2Off.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Star3.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Star3Off.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Chat.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Quote.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Gear2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Gear.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Wrench.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Wrench2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Wrench3.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ScrewDriver.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/HammerAndScrewDriver.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Magic.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Download.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/View.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Noview.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Cloud.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Cloud2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/CloudDown.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/CloudUp.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Location.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Volume0.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Volume1.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Volume2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Volume3.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Key.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ruler.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Power.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Unlock.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Flag.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Tag.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Search.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ZoomOut.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ZoomIn.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Cross.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Check.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Settings.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/SettingsAlt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Feed.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Bug.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Link.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Calendar.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Picker.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/No.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/CommandLine.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Photo.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Printer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Export.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Import.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Run.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Magnet.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/NoMagnet.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ReflectH.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/ReflectV.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Resize2.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Rotate.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Connect.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Disconnect.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Folder.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Man.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Woman.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/People.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Parent.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Notebook.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Diagram.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/BarChart.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/PieChart.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/LineChart.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Apps.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Locked.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ppt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Lab.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Umbrella.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Dry.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ipad.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Iphone.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Jigsaw.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Lamp.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Lamp_alt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Video.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Palm.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Fave.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Help.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Crop.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/BioHazard.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/WheelChair.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Mic.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/MicMute.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/IMac.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Pc.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Cube.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/FullCube.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Font.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Trash.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/NewWindow.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/DockRight.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/DockLeft.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/DockBottom.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/DockTop.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Pallete.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Cart.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Glasses.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Package.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Book.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Books.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Icons.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/List.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Db.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Paper.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/TakeOff.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Landing.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Plane.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Phone.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/HangUp.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/SlideShare.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Twitter.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/TwitterBird.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Skype.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Windows.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Apple.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Linux.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/NodeJs.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/JQuery.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Sencha.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Vim.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/InkScape.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Aumade.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Firefox.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ie.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ie9.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Opera.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Chrome.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Safari.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/LinkedIn.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Flickr.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/GitHub.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/GitHubAlt.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Raphael.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/GRaphael.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Svg.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Usb.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/icon/Ethernet.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/pert/Activity.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/pert/Start.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/state/Start.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/state/End.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/state/State.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/shape/state/Connection.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/ui/LabelEditor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/ui/LabelInplaceEditor.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/decoration/connection/Decorator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/decoration/connection/ArrowDecorator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/decoration/connection/DiamondDecorator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/decoration/connection/CircleDecorator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/decoration/connection/BarDecorator.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/io/Reader.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/io/Writer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/io/svg/Writer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/io/png/Writer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/io/json/Writer.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/io/json/Reader.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/storage/FileStorage.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/storage/GoogleDrive.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/storage/LocalFileStorage.js"> </SCRIPT>
<SCRIPT src="/js/community/draw2d_GPL_5.2.0-dev/src/storage/TideSDKStorage.js"> </SCRIPT>
 -->

<!---------------------- application src ----------------->
<script type="text/javascript" src="/js/community/Schema.js"></SCRIPT>
<script type="text/javascript" src="/js/community/View.js"></SCRIPT>
<script type="text/javascript" src="/js/community/shape/AbstractUnit.js"></SCRIPT>
<script type="text/javascript" src="/js/community/shape/Units.js"></SCRIPT>
<script type="text/javascript" src="/js/community/shape/Connection.js"></SCRIPT>


<style>

    #communitySchemaModal .modal-dialog
    {
        height: 100%;
        width: 100%;
        margin: 0px auto;
        padding: 0;
    }

    #communitySchemaModal .modal-content
    {
        height: 100%;
    }

    #communitySchemaModal .modal-header {
        padding: 10px;
        height: 5%;
        text-align: center;
    }

    #communitySchemaModal .modal-body{
        height: 85%;
        width: 99%;
        padding: 0px;
        margin-top: 0px;
        margin-left: 0px;
    }

    #communitySchemaModal .modal-footer {
        height: 5%;
        padding: 5px;
    }

    #communitySchemaModal .community-schema-modal-body{
        height: 100%;
        overflow: scroll;
    }

    #communitySchemaModalToolbar {
        padding-top: 20px;
        padding-left: 0px;
        margin: 0px;
    }

    .panel-icon {
        top: 0px;
        left: 1px;
    }
</style>


<script type="text/javascript">
	var schema;
	$(document).ready(function() {
		$(window).load(function() {
            $('[data-toggle="tooltip"]').tooltip();

			$('#communitySchemaModal').on('shown.bs.modal', function() {
			    var schemaBodyId = "communitySchemaModalBody";
		        var maxWidth = $("#" + schemaBodyId).width() + 100;
		        var maxHeight = $("#" + schemaBodyId).height() + 100;

				$.radomJsonGet('/communities/loadschema.json', {
					community_id : communityId
				}, function(response) {
					schema = new community.schema.Schema(schemaBodyId,maxWidth, maxHeight, 0, 0);
					schema.enableAutoScroll(true);
					schema.bgUploader = bgUploader;

					if (response.connectionTypes != null)
						schema.setConnectionTypes(response.connectionTypes);

					if (response.schema != null && response.schema.units != null && response.schema.units.length > 0)
						schema.loadFromObject(response.schema);
					else
						schema.setDirector('${community.creator.ikp}', '${fullName}');

					schema.setUndoRedoStackListener(new UndoRedoStackListener());
		            window.onbeforeunload = function (evt) {
		                var message = "Структура не сохранена. Если покините страницу изменения будут потеряны.";
		                if (typeof evt == "undefined") {
		                    evt = window.event;
		                }
		                if (evt) {
		                    evt.returnValue = message;
		                }
		                return message;
		            }

		            var propertiesFigure = null;
		            schema.onSomethingSelected(function(selection){
		                $('#removeButton').prop('disabled', false);
                        $('#toCenterUnitButton').prop('disabled', false);
		                if (selection.getSize() == 1) {
		                    propertiesFigure = selection.get(0);
		                    if (propertiesFigure.type == 'DIRECTOR')
		                        $('#removeButton').prop('disabled', true);
                            $('#communitySchemaModalBody').removeClass('col-md-12').addClass('col-md-10');
                            $('#communitySchemaModalToolbar').removeClass('col-md-0').addClass('col-md-2');
		                    selection.get(0).onDisplayProperties("communitySchemaModalToolbarProperties");
		                }
		                else
		                    emptyToolbar();
		            });
		            schema.onAllUnSelected(function(){
                        $('#removeButton').prop('disabled', true);
                        $('#toCenterUnitButton').prop('disabled', true);
		                emptyToolbar();
		            });
		            var emptyToolbar = function() {
		                if (propertiesFigure != null){
		                    if (propertiesFigure.offDisplayProperties != null)
		                        propertiesFigure.offDisplayProperties();
		                    propertiesFigure = null;
		                }
		                $("#communitySchemaModalToolbarProperties").empty();
                        $('#communitySchemaModalBody').removeClass('col-md-10').addClass('col-md-12');
                        $('#communitySchemaModalToolbar').removeClass('col-md-2').addClass('col-md-0');
		            }
				}, function(response) {
					if (response.message) {
						bootbox.alert(response.message);
					} else {
						bootbox.alert("Произошла ошибка загрузки данных");
					}
					$("#communitySchemaModal").modal('hide');
				});
			});

			$('#communitySchemaModal').on('hidden.bs.modal', function() {
	            window.onbeforeunload = null
				if (schema != null)
					schema.destroy();
			});

		    $('#undoButton').on('click', function (e) {
		    	if (schema != null)
		        	schema.undo();
		    });
		    $(".btn").mouseup(function(){
		        $(this).blur();
		    })

		    $('#redoButton').on('click', function (e) {
		    	if (schema != null)
                	schema.redo();
		    });

	        $('#saveAsPngButton').on('click', function (e) {
	            schema.saveAsPng(function(png){
	                $("#pngLink").attr("href", png)[0].click();
	            });
	        });

	        $('#saveAsSvgButton').on('click', function (e) {
	            schema.saveAsSvg(function(svg, svg64){
	                $("#svgLink").attr("href", "data:image/svg+xml;charset=utf-8," + svg)[0].click();
	            });
	        });

	        $('#saveAsPdfButton').on('click', function (e) {
	            schema.saveAsPdf(function(pdfurl, pdf64){
	                $("#pdfLink").attr("href", pdfurl)[0].click();
	            });
	        });

	        $('#printButton').on('click', function (e) {
	            var printWindow = window.open('', 'schemaprintdiv', 'height=200,width=' + $('#' + schema.id).width());
	            printWindow.document.write('<html><head><title>Структура объединения</title>');
	            printWindow.document.write(schema.getBgImageCss());
	            printWindow.document.write('</head><body>');
	            printWindow.document.write($('#' + schema.id).html());
	            printWindow.document.write('</body></html>');
	            printWindow.document.close();
	            printWindow.print();
	        });

	        $('#addNewUnitButton').on('click', function (e) {
	            schema.addNewUnit();
	        });

            $('#toCenterUnitButton').on('click', function (e) {
                schema.toCenterUnit();
            });

	        $('#removeButton').on('click', function (e) {
	            schema.removeSelected();
	        });

	        $('#changeBgButton').on('click', function (e) {
	            schema.changeBgForm();
	        });

	        $('#clearBgButton').on('click', function (e) {
	            schema.clearBg();
	        });

            $('#zoomIn').on('click', function (e) {
                schema.zoomIn();
            });

            $('#zoomDefault').on('click', function (e) {
                schema.zoomDefault();
            });

            $('#zoomOut').on('click', function (e) {
                schema.zoomOut();
            });
		});
	});

	editCommunitySchema = function() {
		$('#communitySchemaModal').modal({
			show : true
		});
	}

    bgUploader = function(event) {
			var path = $("#" + event.target.id + ' input[type=file]').val();
			if (path != "") {
				var extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
				if ($.inArray(extension, ["jpg", "jpeg", "png", "bmp"]) >= 0) {
					event.preventDefault();
					var formData = new FormData(event.target);
					$.ajax({
						url: "/images/upload/schema/0.json",
						type: 'POST',
						data: formData,
						async: false,
						cache: false,
						contentType: false,
						processData: false,
						success: function (response) {
							if (response.result == "error") {
								if (response.message) {
									bootbox.alert(response.message);
								} else {
									bootbox.alert("Ошибка загрузки фото");
								}
							} else {
								if (response.image != null)
									schema.setBgImageUrlInCommand(response.image);
								else
									bootbox.alert("Ошибка загрузки фото");
							}
						},
						error: function() {
							bootbox.alert("Ошибка загрузки фото");
						}
					});
				} else {
					bootbox.alert("Разрешена загрузка файлов с расширениями jpeg, jpg, png, bmp");
				}

			}

        return null;
    }

	saveCommunitySchemaModal = function() {
		$.radomJsonPost("/group/" + communityId + "/saveschema.json",
                schema.saveToJson(),
                function(response) {
                    $("#communitySchemaModal").modal('hide');
                    bootbox.alert("Структура удачно сохранена");
                },
                function(response) {
                    if (response.message) {
                        bootbox.alert(response.message);
                    } else {
                        bootbox.alert("Произошла ошибка сохранения структуры");
                    }
                },
                {
                    contentType: "application/json"
                }
        );
	};

    UndoRedoStackListener = Class.extend({
        stackChanged:function(event)
        {
            $('#undoButton').prop('disabled', !event.getStack().canUndo());
            $('#redoButton').prop('disabled', !event.getStack().canRedo());
        }
    });
</script>

<div class="modal modal-fullscreen" id="communitySchemaModal" tabindex="-1" role="dialog" aria-labelledby="communitySchemaModalLabel" aria-hidden="true"  data-backdrop="static" data-keyboard="false">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="communitySchemaModalLabel">Редактирование структурной схемы объединения ${community.name}</h4>
            </div>
            <div class="modal-header">
                <div class="btn-group-sm btn-group-sm center-block community-schema-buttons-group">
                    <button type="button" class="btn btn-primary" id="addNewUnitButton" data-toggle="tooltip" data-placement="top" data-original-title="Добавить структурную единицу"><span class="glyphicon glyphicon-plus panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="toCenterUnitButton" data-toggle="tooltip" data-placement="top" data-original-title="Отцентрировать" disabled="true"><span class="glyphicon glyphicon-screenshot panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="removeButton" data-toggle="tooltip" data-placement="top" data-original-title="Удалить" disabled="true"><span class="glyphicon glyphicon-trash panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="changeBgButton" data-toggle="tooltip" data-placement="top" data-original-title="Сменить фон"><span class="glyphicon glyphicon-picture panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="clearBgButton" data-toggle="tooltip" data-placement="top" data-original-title="Очистить фон"><span class="glyphicon glyphicon-erase panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="saveAsPngButton" data-toggle="tooltip" data-placement="top" data-original-title="Экспорт в png"><span class="glyphicon glyphicon-export panel-icon"> PNG</span></button>
                    <button type="button" class="btn btn-primary" id="saveAsSvgButton" data-toggle="tooltip" data-placement="top" data-original-title="Экспорт в svg"><span class="glyphicon glyphicon-export panel-icon"> SVG</span></button>
                    <button type="button" class="btn btn-primary" id="printButton" data-toggle="tooltip" data-placement="top" data-original-title="Экспорт в Pdf/Печать"><span class="glyphicon glyphicon-print panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="undoButton" data-toggle="tooltip" data-placement="top" data-original-title="Отменить" disabled="true"><span class="glyphicon glyphicon-backward panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="redoButton" data-toggle="tooltip" data-placement="top" data-original-title="Повторить" disabled="true"><span class="glyphicon glyphicon-forward panel-icon"></span></button>
                    <button type="button" class="btn btn-primary" id="zoomIn" data-toggle="tooltip" data-placement="top" data-original-title="Увеличить масштаб"><span class="glyphicon glyphicon-zoom-in panel-icon"></span></button>
                    <button type="button" class="btn btn-primary btnZoomDefault" id="zoomDefault" data-toggle="tooltip" data-placement="top" data-original-title="Стандартный масштам"><span class="panel-icon">1 : 1</span></button>
                    <button type="button" class="btn btn-primary" id="zoomOut" data-toggle="tooltip" data-placement="top" data-original-title="Уменьшить масштаб"><span class="glyphicon glyphicon-zoom-out panel-icon"></span></button>
                </div>
            </div>
            <div class="modal-body row" >
                <div class="col-md-12 community-schema-modal-body" id="communitySchemaModalBody">

                </div>
                <div class="col-md-0" id="communitySchemaModalToolbar">
                    <a href="" id="pngLink" download="schema.png"></a>
                    <a href="" id="svgLink" download="schema.svg"></a>
                    <a href="" id="pdfLink" download="schema.pdf"></a>
                    <br>
                    <div id="communitySchemaModalToolbarProperties">
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Отмена</button>
                <button type="button" class="btn btn-primary" onclick="saveCommunitySchemaModal();">Сохранить структуру</button>
            </div>
        </div>
    </div>
</div>

<!-- members selector -->

<style>
    #communitySchemaPeopleSelectorModal .modal-dialog
    {
        width: 60%;
    }

    #memberList{
        max-height: 500px;
        overflow-y: scroll;
        overflow-x: hidden;
    }
</style>



<div class="modal" id="communitySchemaPeopleSelectorModal" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h5 class="modal-title" id="communitySchemaPeopleSelectorModalTitle"></h5>
            </div>
            <div class="modal-body">
                <div class="btn-group" data-toggle="buttons" id="radioButtons">
                    <label class="btn btn-default">
                        <input checked="checked" type="radio" id="radioOnlyMembers" name="options">Сотрудники
                    </label>
                    <label class="btn btn-default">
                        <input type="radio" name="options" id="radioNotMembers">Не сотрудники
                    </label>
                    <label class="btn btn-default">
                        <input type="radio" name="options" id="radioAllMembers">Все
                    </label>
                    <label class="btn btn-default">
                        <input type="radio" name="options" id="radioAddNewMember">Создать нового сотрудника
                    </label>
                    <br><br>
                </div>

                <div id="addNewMemberDiv" hidden>
                    <div class="form-group">
                        <label for="newMemberFullName">Фамилия и имя нового сотрудника</label>
                        <input type="text" class="form-control" id="newMemberFullName" placeholder="Введите фамилию и имя нового сотрудника">
                    </div>
                    <div class="form-group">
                        <label for="newMemberEmail">Электроннай адрес</label>
                        <input type="email" class="form-control" id="newMemberEmail" placeholder="Введите электроннай адрес">
                    </div>
                    <button type="submit" class="btn btn-default" id="addNewMemberButton">Добавить</button>
                </div>

                <div id="memberListWithQueryDiv">
                <div class="form-group">
                    <input class="form-control" type="text" id="query" placeholder="Начните вводить фамилию или имя" />
                </div>
                <hr/>
                <div class="members-list" id="memberList"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-sm btn-default" data-dismiss="modal">Отмена</button>
                <button type="button" class="btn-sm btn-primary" id="okButton">Применить</button>
            </div>
        </div>
    </div>
</div>


<script id="community-schema-unit-people-template" type="x-tmpl-mustache">
<div class="row member-item" data-sharer-ikp="{{sharer.ikp}}" data-sharer-email="{{sharer.email}}" data-sharer-fullname="{{sharer.fullName}}">
	<div class="col-xs-4">
		<div class="row">
			<div class="col-xs-12">
				<a class="sharer-item-avatar-link" href="{{sharer.link}}">
					<img style="display : block; width : 141px; height : 141px;" src="{{sharer.avatar}}" class="img-thumbnail">
					{{#sharer.online}}
						<img src="/i/icon-online.png" class="sharer-item-online-icon">
					{{/sharer.online}}
					{{^sharer.online}}
						<img src="/i/icon-offline.png" class="sharer-item-online-icon">
					{{/sharer.online}}
				</a>
				{{#sharer.online}}
					<span class="sharer-item-online-status">В сети</span>
				{{/sharer.online}}
				{{^sharer.online}}
					<span class="sharer-item-online-status text-muted">Не в сети</span>
				{{/sharer.online}}
			</div>
		</div>
	</div>
	<div class="col-xs-8">
		<h3><a href="{{sharer.link}}">{{sharer.fullName}}</a></h3>
		<p class="text-muted">
			{{currentStatus}}
		</p>
		<hr>
		{{#selectManager}}
			<a class="btn btn-primary select-link" href="#">Назначить руководителем</a>
		{{/selectManager}}
		{{#notAMember}}
			<a class="btn btn-primary add-link" href="#">Добавить</a>
		{{/notAMember}}
		{{#aMember}}
			<a class="btn btn-primary remove-link" href="#">Исключить</a>
		{{/aMember}}
	</div>
</div>
<hr/>
</script>

<script type="text/javascript">
var CommunitySchemaPeopleSelector = {
    template: $('#community-schema-unit-people-template').html(),
    templateParsed: false,

    selectedManager: null,

    modalWindowId: null,
    modalWindowTitle: null,
    onShownModalWindow: null,
    onHiddenModalWindow: null,

    members: null,

    selectManager: function (unit, callback) {
        var _this = this;

        this.modalWindowId = "communitySchemaPeopleSelectorModal";
        this.modalWindowTitle = "Выбор руководителя структурной единицы <b>" + unit.getUnitName() + "<b>"
        this.selectedManager = null;
        this.members = null;

        $("#okButton").hide();
        $("#radioButtons").hide();
        $("#addNewMemberDiv").hide();
        $("memberListWithQueryDiv").hide();

        this.onShownModalWindow = function () {
        };
        this.onHiddenModalWindow = function () {
            callback(_this.selectedManager);
        }

        this.initWindow();
    },

    selectMembers: function (unit, callback) {
        var _this = this;

        this.modalWindowId = "communitySchemaPeopleSelectorModal";
        this.modalWindowTitle = "Выбор сотрудников структурной единицы <b>" + unit.getUnitName() + "<b>"
        this.members = unit.getMembers();
        $("#okButton").show();
        $("#radioButtons").show();

        var applyChanges = function(){
            $("#" + _this.modalWindowId).modal('hide');
            callback(_this.members);
        }

        var addNewMemberCallback = function(){
            var member = {fullName: $("#newMemberFullName").val(), email : $("#newMemberEmail").val(), ikp : null};
            _this.addMember(member);
            $("#newMemberFullName").val("");
            $("#newMemberEmail").val("");
        }

        this.onShownModalWindow = function () {
        };
        this.onHiddenModalWindow = function () {
            $("#okButton").off("click", applyChanges);
            $("#addNewMemberButton").off("click", addNewMemberCallback);
        }
        $("#okButton").on("click", applyChanges);
        $("#addNewMemberButton").on("click", addNewMemberCallback);

        $('#radioOnlyMembers').change(function(){$("#addNewMemberDiv").hide(); $("#memberListWithQueryDiv").show(); _this.initScrollListener();});
        $('#radioNotMembers').change(function(){$("#addNewMemberDiv").hide();$("#memberListWithQueryDiv").show(); _this.initScrollListener();});
        $('#radioAllMembers').change(function(){$("#addNewMemberDiv").hide();$("#memberListWithQueryDiv").show(); _this.initScrollListener();});
        $('#radioAddNewMember').change(function(){$("#addNewMemberDiv").show();$("#memberListWithQueryDiv").hide();});

        this.initWindow();
    },

    addMember : function(member){
        this.members.push(member);
    },

    removeMember : function(member){
        for (var i = 0; i < this.members.length; i++){
            if (this.members[i].ikp != null && member.ikp != null && this.members[i].ikp == member.ikp ||
                    this.members[i].ikp == null && member.ikp == null && this.members[i].email == member.email)
            {
                this.members.splice(i, 1);
                return;
            }
        }
    },

    isSelectManager: function ()
    {
        return this.members == null;
    },

    isAMember: function (sharer)
    {
        if (this.members == null)
            return false;

        for (var i = 0; i < this.members.length; i++){
            if (this.members[i].ikp != null && sharer.ikp != null && this.members[i].ikp == sharer.ikp ||
                    this.members[i].ikp == null && sharer.ikp == null && this.members[i].email == sharer.email)
                return true;
        }
        return false;

    },

    isNotAMember: function (sharer)
    {
        if (this.members == null)
            return false;
        return !this.isAMember(sharer);
    },

    isOnlyMembers : function () {
        return $('#radioOnlyMembers').is(':checked');
    },

    isNotMembers : function (){
        return $('#radioNotMembers').is(':checked');
    },

    isAllMembers : function (){
        return $('#radioAllMembers').is(':checked');
    },

    getMembersIkpList : function(){
        if (this.members == null || this.isAllMembers())
            return null;

        var ikps = [];
        for (var i = 0; i < this.members.length; i++) {
            if (this.members[i].ikp != null)
                ikps.push(this.members[i].ikp);
        }
        return ikps;
    },

    appendNewMembers : function(){
        if (this.members != null && (this.isAllMembers() || this.isOnlyMembers())) {
            for (var i = 0; i < this.members.length; i++) {
            	if (this.members[i].ikp == null)
                	this.append({ikp : null, fullName : this.members[i].fullName, avatar : 'none', email : this.members[i].email}, $("div.members-list"));
            }
        }
    },

    initScrollListener : function() {
        $("div.members-list").empty();
        this.appendNewMembers();

        ScrollListener.loading = false;
        var _this = this;
        ScrollListener.init("/communities/schema/possible_members.json", "post", function() {
            var params = {};
            var query = $("input#query").val();
            if (query) {
                params.query = query;
            }

            var ikps = _this.getMembersIkpList();
            if (ikps != null){
                params.ikps = JSON.stringify(ikps);
                params.ikpsOnly = _this.isOnlyMembers();
            }
            return params;
        }, function() {

        }, function(response) {
            $.each(response, function(index, sharer) {
                _this.append(sharer, $("div.members-list"));
            });
        }, null, null, "memberList");
    },

    initWindow : function(){
        var _this = this;

        $("#" + this.modalWindowId + "Title").html(this.modalWindowTitle);
        $("#" + this.modalWindowId).on('shown.bs.modal', function () {
            $("div.members-list").empty();
            $("input#query").val("");

            if (typeof _this.onShownModalWindow == 'function')
                _this.onShownModalWindow();

            _this.initScrollListener();
        });

        $("#" + this.modalWindowId).on('hidden.bs.modal', function () {
            $("#" + this.modalWindowId).off('hidden.bs.modal');
            $("#" + this.modalWindowId).off('shown.bs.modal');

            if (typeof _this.onHiddenModalWindow == 'function')
                _this.onHiddenModalWindow();
        });

        $("#" + this.modalWindowId).modal({
            show : true
        });

        $("input#query").callbackInput(500, 3, function() {
            _this.initScrollListener();
        });

        $("input#query").radomTooltip({
            title : "Фильтр активируется после ввода минимум трех символов",
            placement : "top"
        });
    },

    getMarkup : function(sharer) {
        if (!this.templateParsed) {
            Mustache.parse(this.template);
            this.templateParsed = true;
        }
        sharer.avatar = Images.getResizeUrl(sharer.avatar, "c141");
        var model = {
            sharer          : sharer,
            selectManager   : this.isSelectManager(),
            aMember       : this.isAMember(sharer),
            notAMember    : this.isNotAMember(sharer)
        };

        var markup = Mustache.render(this.template, model);
        var $markup = $(markup);

        var _this = this;
        $markup.find("a.select-link").click(function(){
            var $item = $(this).parents(".member-item");
            _this.selectedManager = {
                managerIkp      : $item.attr("data-sharer-ikp"),
                managerFullName : $item.attr("data-sharer-fullname")
            };
            $('#' + _this.modalWindowId).modal('hide');
            return false;
        });

        var createMember = function ($item){
            var member =
            {
                fullName : $item.attr("data-sharer-fullname"),
                ikp : $item.attr("data-sharer-ikp"),
                email : $item.attr("data-sharer-email")
            };

            if (member.ikp == "")
                member.ikp = null;
            return member;
        }

        var addCallback = function(){
            var $item = $(this).parents(".member-item");
            _this.addMember(createMember($item));

            if (_this.isAllMembers()) {
                $(this).replaceWith($('<a class="btn btn-primary remove-link" href="#">Исключить</a>'));
                $markup.find("a.remove-link").click(removeCallback);
            }
            else{
                $item.next('hr').remove();
                $item.remove();
            }

            return false;
        };

        var removeCallback = function(){
            var $item = $(this).parents(".member-item");
            _this.removeMember(createMember($item));

            if (_this.isAllMembers()) {
                $(this).replaceWith($('<a class="btn btn-primary add-link" href="#">Добавить</a>'));
                $markup.find("a.add-link").click(addCallback);
            }
            else {
                $item.next('hr').remove();
                $item.remove();
            }
            return false;
        };

        $markup.find("a.add-link").click(addCallback);
        $markup.find("a.remove-link").click(removeCallback);

        return $markup;
    },

    append : function(sharer, $list) {
        var $item = $("div.member-item[data-sharer-ikp=" + sharer.ikp + "]");
        if ($item.length == 0) {
            $list.append(this.getMarkup(sharer));
        }
    }

};

</script>
