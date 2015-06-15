package com.RobD.Moba.Utils;

public class Zoom {

		public static final float ZOOM_NEAR = 10F;
		public static final float ZOOM_MID = 4.00F;
		public static final float ZOOM_FAR = 1.0F;
		
		public float targetZoom;
		public float currentZoom;
		
		public Zoom(){
			targetZoom = ZOOM_FAR;
			currentZoom = ZOOM_FAR;
		}

		public float getTargetZoom() {
			return targetZoom;
		}

		public void setTargetZoom(float targetZoom) {
			this.targetZoom = targetZoom;
		}

		public float getCurrentZoom() {
			return currentZoom;
		}

		public void setCurrentZoom(float currentZoom) {
			this.currentZoom = currentZoom;
		}
	
}
