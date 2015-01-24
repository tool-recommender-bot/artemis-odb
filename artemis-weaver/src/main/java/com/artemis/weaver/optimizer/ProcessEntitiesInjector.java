package com.artemis.weaver.optimizer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.artemis.meta.ClassMetadata;
import com.artemis.meta.ClassMetadata.OptimizationType;

public final class ProcessEntitiesInjector implements Opcodes {

	private final ClassReader cr;
	private final ClassMetadata meta;
	private final ClassWriter cw;

	public ProcessEntitiesInjector(ClassReader cr, ClassMetadata meta) {
		this.cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		this.cr = cr;
		this.meta = meta;
	}

	public ClassReader transform() {
		return new ClassReader(injectMethods());
	}

	private byte[] injectMethods() {
		injectProcessEntities();
		cr.accept(cw, 0);
		return cw.toByteArray();
	}
	
	private void injectProcessEntities() {
		String owner = meta.type.getInternalName();

		MethodVisitor mv = cw.visitMethod(ACC_PROTECTED | ACC_FINAL,
				"processEntities", "(Lcom/artemis/utils/IntBag;)V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);


		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/IntBag", "getData", "()[I");
		mv.visitVarInsn(ASTORE, 2);
		Label l1 = new Label();
		mv.visitLabel(l1);


		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "com/artemis/EntitySystem", "flyweight", "Lcom/artemis/Entity;");
		mv.visitVarInsn(ASTORE, 3);
		Label l2 = new Label();
		mv.visitLabel(l2);


		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 4);
		Label l3 = new Label();
		mv.visitLabel(l3);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "com/artemis/utils/IntBag", "size", "()I");
		mv.visitVarInsn(ISTORE, 5);
		Label l4 = new Label();
		mv.visitLabel(l4);
		Label l5 = new Label();
		mv.visitJumpInsn(GOTO, l5);
		Label l6 = new Label();
		mv.visitLabel(l6);


		mv.visitFrame(Opcodes.F_FULL, 6, new Object[] {
				"com/artemis/EntitySystem",
				"com/artemis/utils/IntBag",
				"[I", "com/artemis/Entity",
				Opcodes.INTEGER, Opcodes.INTEGER},
				0, new Object[] {});

		mv.visitVarInsn(ALOAD, 3);
		mv.visitVarInsn(ALOAD, 2);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitInsn(IALOAD);
		mv.visitFieldInsn(PUTFIELD, "com/artemis/Entity", "id", "I");
		Label l7 = new Label();
		mv.visitLabel(l7);


		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 3);
		mv.visitMethodInsn(invocation(meta.sysetemOptimizable), owner, "process", "(Lcom/artemis/Entity;)V");
		Label l8 = new Label();
		mv.visitLabel(l8);


		mv.visitIincInsn(4, 1);
		mv.visitLabel(l5);
		mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ILOAD, 5);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitJumpInsn(IF_ICMPGT, l6);
		Label l9 = new Label();
		mv.visitLabel(l9);
		mv.visitLineNumber(50, l9);
		mv.visitInsn(RETURN);
		Label l10 = new Label();
		mv.visitLabel(l10);
		mv.visitLocalVariable("this", meta.type.toString(), null, l0, l10, 0);
		mv.visitLocalVariable("entities", "Lcom/artemis/utils/IntBag;", null, l0, l10, 1);
		mv.visitLocalVariable("array", "[I", null, l1, l10, 2);
		mv.visitLocalVariable("e", "Lcom/artemis/Entity;", null, l2, l10, 3);
		mv.visitLocalVariable("i", "I", null, l3, l9, 4);
		mv.visitLocalVariable("s", "I", null, l4, l9, 5);
		mv.visitEnd();
	}

	private static int invocation(OptimizationType systemOptimization) {
		switch (systemOptimization) {
		case FULL:
			return INVOKESPECIAL; 
		case SAFE:
			return INVOKEVIRTUAL;
		case NOT_OPTIMIZABLE:
			assert false;
		default:
			throw new RuntimeException("Missing case: " + systemOptimization);
		
		}
	}
}
