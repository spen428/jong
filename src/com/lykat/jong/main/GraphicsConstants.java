package com.lykat.jong.main;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public final class GraphicsConstants {

	/* Object Sizes */
	public static final float TILE_WIDTH_MM = 18.2f;
	public static final float TILE_HEIGHT_MM = 25.3f;
	public static final float TILE_THICKNESS_MM = 15.5f * 0.75f;
	public static final float TILE_GAP_MM = 3f;

	public static final float RIICHI_WIDTH_MM = TILE_WIDTH_MM * 3.5f;
	public static final float RIICHI_THICKNESS_MM = TILE_THICKNESS_MM * 0.3f;
	public static final float RIICHI_HEIGHT_MM = RIICHI_THICKNESS_MM * 0.3f;

	public static final float PLAYING_SURFACE_RADIUS_MM = 350.0f;
	public static final float PLAYING_SURFACE_THICKNESS_MM = 1f;

	/* Object Positions */

	/**
	 * Distance of the camera from the playing surface in the Z plane.
	 */
	public static final float PLAYER_CAMERA_Z_OFFSET_MM = PLAYING_SURFACE_RADIUS_MM * 0.75f;
	public static final float PLAYER_CAMERA_Y_OFFSET_MM = PLAYING_SURFACE_RADIUS_MM * 1.2f;
	public static final float OVERHEAD_CAMERA_Z_OFFSET_MM = PLAYER_CAMERA_Z_OFFSET_MM * 2.1f;

	/**
	 * Offset from the centre of table in the Y plane.
	 */
	public static final float HAND_TILES_Y_OFFSET_MM = PLAYING_SURFACE_RADIUS_MM * 0.75f;

	/**
	 * Offset from the centre of table in the Y plane.
	 */
	public static final float DISCARD_TILES_Y_OFFSET_MM = -TILE_WIDTH_MM * 4;

	/**
	 * Offset from the centre of table in the Y plane.
	 */
	public static final float RIICHI_STICK_Y_OFFSET_MM = PLAYING_SURFACE_RADIUS_MM * 0.25f;

	/* Static Models */
	private static final ModelBuilder mb = new ModelBuilder();
	private static final Material mat = new Material(
			ColorAttribute.createDiffuse(Color.WHITE));
	private static final int attr = Usage.Position | Usage.Normal;

	public static final Model MODEL_TILE_FLAT = mb.createBox(TILE_WIDTH_MM,
			TILE_HEIGHT_MM, TILE_THICKNESS_MM, mat, attr);

	public static final Model MODEL_TILE_FLAT_90 = mb.createBox(TILE_HEIGHT_MM,
			TILE_WIDTH_MM, TILE_THICKNESS_MM, mat, attr);

	public static final Model MODEL_TILE_STAND = mb.createBox(TILE_WIDTH_MM,
			TILE_THICKNESS_MM, TILE_HEIGHT_MM, mat, attr);

	public static final Model MODEL_TILE_STAND_90 = mb.createBox(
			TILE_THICKNESS_MM, TILE_WIDTH_MM, TILE_HEIGHT_MM, mat, attr);

	public static final Model MODEL_TILE_SIDE = mb.createBox(TILE_HEIGHT_MM,
			TILE_THICKNESS_MM, TILE_WIDTH_MM, mat, attr);

	private GraphicsConstants() {
	}

}
