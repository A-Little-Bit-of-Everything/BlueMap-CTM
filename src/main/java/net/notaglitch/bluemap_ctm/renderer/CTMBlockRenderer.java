package net.notaglitch.bluemap_ctm.renderer;

import de.bluecolored.bluemap.core.map.TextureGallery;
import net.notaglitch.bluemap_ctm.util.ArrayTileModelAccessor;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.texture.Texture;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.BlockEntity;
import de.bluecolored.bluemap.core.world.BlockState;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import de.bluecolored.bluemap.core.world.block.ExtendedBlock;
import net.notaglitch.bluemap_ctm.layout.FaceOrientation;
import net.notaglitch.bluemap_ctm.layout.FullLayout;
import net.notaglitch.bluemap_ctm.layout.PiecedLayout;
import net.notaglitch.bluemap_ctm.layout.TextureConnections;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CTMBlockRenderer implements BlockRenderer {

    private final BlockRenderer delegate;
    private final ResourcePack resourcePack;
    private final TextureGallery textureGallery;
    private final RenderSettings renderSettings;

    private final Map<BlockState, LayoutInfo> layoutCache = new ConcurrentHashMap<>();

    private enum LayoutType {
        FULL,
        PIECED,
        HORIZONTAL
    }

    private static class LayoutInfo {
        final LayoutType type;
        final float uScale;
        final float vScale;

        LayoutInfo(LayoutType type, float uScale, float vScale) {
            this.type = type;
            this.uScale = uScale;
            this.vScale = vScale;
        }
    }

    public CTMBlockRenderer(BlockRenderer delegate, ResourcePack resourcePack, TextureGallery textureGallery, RenderSettings renderSettings) {
        this.delegate = delegate;
        this.resourcePack = resourcePack;
        this.textureGallery = textureGallery;
        this.renderSettings = renderSettings;
    }

    @Override
    public void render(BlockNeighborhood block, Variant variant, TileModelView tileModel, Color blockColor) {
        TileModel tm = tileModel.getTileModel();
        int startFace = tm.size();

        // Delegate to underlying renderer first
        delegate.render(block, variant, tileModel, blockColor);

        int endFace = tm.size();
        if (endFace <= startFace) return;

        // Check if current block or its disguise is a connecting block
        BlockState renderState = block.getBlockState();
        BlockEntity be = block.getBlockEntity();
        if (be != null) {
            BlockState disguised = DisguiseHelper.getDisguisedState(be);
            if (disguised != null) {
                renderState = disguised;
            }
        }

        String path = renderState.getId().getValue();
        if (!path.contains("connecting")) {
            return; // Not a connecting block, keep default render output
        }

        float[] positions = ArrayTileModelAccessor.getPositions(tm);
        float[] uvs = ArrayTileModelAccessor.getUvs(tm);
        if (positions == null || uvs == null) return;

        int fiPos = ArrayTileModelAccessor.getFI_POSITION();
        int fiUv = ArrayTileModelAccessor.getFI_UV();

        LayoutInfo layout = getLayoutInfo(renderState);

        // Process faces in pairs of triangles that make up a quad
        for (int faceIndex = startFace; faceIndex < endFace; faceIndex += 2) {
            int pIdx = faceIndex * fiPos;

            // Vertex 0, 1, 2 coordinates of triangle 1
            float x0 = positions[pIdx + 0];
            float y0 = positions[pIdx + 1];
            float z0 = positions[pIdx + 2];

            float x1 = positions[pIdx + 3];
            float y1 = positions[pIdx + 4];
            float z1 = positions[pIdx + 5];

            float x2 = positions[pIdx + 6];
            float y2 = positions[pIdx + 7];
            float z2 = positions[pIdx + 8];

            // Normal = (V1 - V0) x (V2 - V0)
            float v10x = x1 - x0;
            float v10y = y1 - y0;
            float v10z = z1 - z0;

            float v20x = x2 - x0;
            float v20y = y2 - y0;
            float v20z = z2 - z0;

            float nx = v10y * v20z - v10z * v20y;
            float ny = v10z * v20x - v10x * v20z;
            float nz = v10x * v20y - v10y * v20x;

            FaceOrientation orient = FaceOrientation.fromNormal(nx, ny, nz);

            // Query connections
            boolean top = isConnected(block, renderState, orient.getTopDx(), orient.getTopDy(), orient.getTopDz());
            boolean right = isConnected(block, renderState, orient.getRightDx(), orient.getRightDy(), orient.getRightDz());
            boolean bottom = isConnected(block, renderState, orient.getBottomDx(), orient.getBottomDy(), orient.getBottomDz());
            boolean left = isConnected(block, renderState, orient.getLeftDx(), orient.getLeftDy(), orient.getLeftDz());

            boolean topRight = isConnected(block, renderState, orient.getTopRightDx(), orient.getTopRightDy(), orient.getTopRightDz());
            boolean bottomRight = isConnected(block, renderState, orient.getBottomRightDx(), orient.getBottomRightDy(), orient.getBottomRightDz());
            boolean bottomLeft = isConnected(block, renderState, orient.getBottomLeftDx(), orient.getBottomLeftDy(), orient.getBottomLeftDz());
            boolean topLeft = isConnected(block, renderState, orient.getTopLeftDx(), orient.getTopLeftDy(), orient.getTopLeftDz());

            float minU, minV;
            if (layout.type == LayoutType.FULL) {
                int cIndex = FullLayout.getIndex(top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft);
                int tileX = FullLayout.getTileX(cIndex);
                int tileY = FullLayout.getTileY(cIndex);
                minU = tileX * layout.uScale;
                minV = tileY * layout.vScale;
            } else if (layout.type == LayoutType.HORIZONTAL) {
                int hIndex = (left && right) ? 2 : (right ? 1 : (left ? 3 : 0));
                minU = hIndex * layout.uScale;
                minV = 0f;
            } else { // PIECED
                TextureConnections connections = new TextureConnections(
                        top, topRight, right, bottomRight, bottom, bottomLeft, left, topLeft
                );
                int tileIndex = PiecedLayout.getTileIndex(connections);
                if (tileIndex < 0) {
                    tileIndex = PiecedLayout.getCornerSprite(top, left, topLeft);
                }
                minU = tileIndex * layout.uScale;
                minV = 0f;
            }

            // Remap triangle 1 (vertices 0, 1, 2)
            int uIdx1 = faceIndex * fiUv;
            for (int k = 0; k < 3; k++) {
                int uPos = uIdx1 + k * 2;
                int vPos = uPos + 1;
                float uLocal = normalizeUv(uvs[uPos]);
                float vLocal = normalizeUv(uvs[vPos]);
                uvs[uPos] = minU + uLocal * layout.uScale;
                uvs[vPos] = minV + vLocal * layout.vScale;
            }

            // Remap triangle 2 (vertices 0, 1, 2) if present
            if (faceIndex + 1 < endFace) {
                int uIdx2 = (faceIndex + 1) * fiUv;
                for (int k = 0; k < 3; k++) {
                    int uPos = uIdx2 + k * 2;
                    int vPos = uPos + 1;
                    float uLocal = normalizeUv(uvs[uPos]);
                    float vLocal = normalizeUv(uvs[vPos]);
                    uvs[uPos] = minU + uLocal * layout.uScale;
                    uvs[vPos] = minV + vLocal * layout.vScale;
                }
            }
        }
    }

    private static float normalizeUv(float val) {
        float norm = val - (float) Math.floor(val);
        if (norm == 0f && val >= 1.0f) return 1.0f;
        return norm;
    }

    private LayoutInfo getLayoutInfo(BlockState state) {
        return layoutCache.computeIfAbsent(state, s -> {
            try {
                de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState bs = resourcePack.getBlockState(s);
                if (bs != null) {
                    final Texture[] found = new Texture[1];
                    bs.forEach(s, 0, 0, 0, v -> {
                        if (found[0] != null) return;
                        Model model = v.getModel().getResource(resourcePack.getModels()::get);
                        if (model != null && model.getTextures() != null) {
                            for (var tv : model.getTextures().values()) {
                                var tp = tv.getTexturePath(model.getTextures()::get);
                                if (tp != null) {
                                    Texture tex = tp.getResource(resourcePack.getTextures()::get);
                                    if (tex != null) {
                                        found[0] = tex;
                                        return;
                                    }
                                }
                            }
                        }
                    });

                    if (found[0] != null) {
                        BufferedImage img = found[0].getTextureImage();
                        if (img != null) {
                            int w = img.getWidth();
                            int h = img.getHeight();
                            if (w == 80 && h == 16) {
                                return new LayoutInfo(LayoutType.PIECED, 0.2f, 1.0f);
                            } else if (w == 64 && h == 16) {
                                return new LayoutInfo(LayoutType.HORIZONTAL, 0.25f, 1.0f);
                            } else if (w == h) {
                                return new LayoutInfo(LayoutType.FULL, 1f / 8f, (w / 8f) / (float) h);
                            } else if (w * 6 == h * 8) {
                                return new LayoutInfo(LayoutType.FULL, 1f / 8f, 1f / 6f);
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {}
            // Default fallback is FULL layout for connecting blocks (8x8 tiles or 8x6 tiles)
            return new LayoutInfo(LayoutType.FULL, 0.125f, 0.125f);
        });
    }

    private boolean isConnected(BlockNeighborhood block, BlockState current, int dx, int dy, int dz) {
        ExtendedBlock neighbor = block.getNeighborBlock(dx, dy, dz);
        if (neighbor == null) return false;

        BlockState neighborState = neighbor.getBlockState();
        if (neighborState.isAir()) return false;

        BlockEntity be = neighbor.getBlockEntity();
        if (be != null) {
            BlockState disguised = DisguiseHelper.getDisguisedState(be);
            if (disguised != null) {
                neighborState = disguised;
            }
        }

        return isSameConnectingGroup(current, neighborState);
    }

    private boolean isSameConnectingGroup(BlockState a, BlockState b) {
        if (a.equals(b)) return true;
        if (a.getId().equals(b.getId())) return true;

        if (!a.getId().getNamespace().equals(b.getId().getNamespace())) return false;

        String pathA = a.getId().getValue();
        String pathB = b.getId().getValue();

        String stemA = cleanStem(pathA);
        String stemB = cleanStem(pathB);

        return stemA.equals(stemB);
    }

    private String cleanStem(String path) {
        return path.replace("_slab", "").replace("_stairs", "");
    }

}
